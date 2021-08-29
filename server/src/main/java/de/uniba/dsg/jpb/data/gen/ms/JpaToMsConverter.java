package de.uniba.dsg.jpb.data.gen.ms;

import de.uniba.dsg.jpb.data.gen.DataProvider;
import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.data.model.jpa.AddressEmbeddable;
import de.uniba.dsg.jpb.data.model.jpa.CarrierEntity;
import de.uniba.dsg.jpb.data.model.jpa.CustomerEntity;
import de.uniba.dsg.jpb.data.model.jpa.DistrictEntity;
import de.uniba.dsg.jpb.data.model.jpa.EmployeeEntity;
import de.uniba.dsg.jpb.data.model.jpa.OrderEntity;
import de.uniba.dsg.jpb.data.model.jpa.OrderItemEntity;
import de.uniba.dsg.jpb.data.model.jpa.PaymentEntity;
import de.uniba.dsg.jpb.data.model.jpa.ProductEntity;
import de.uniba.dsg.jpb.data.model.jpa.StockEntity;
import de.uniba.dsg.jpb.data.model.jpa.WarehouseEntity;
import de.uniba.dsg.jpb.data.model.ms.AddressData;
import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.PaymentData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.StockData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts a JPA data model to a MicroStream data model. Converting an existing model ensures that
 * both models are alike and at the same time eliminates the need for duplicate model generation
 * code.
 *
 * @see JpaDataGenerator
 * @author Benedikt Full
 */
public class JpaToMsConverter
    implements DataProvider<WarehouseData, EmployeeData, ProductData, CarrierData> {

  private final List<ProductEntity> productEntities;
  private final List<CarrierEntity> carrierEntities;
  private final List<WarehouseEntity> warehouseEntities;
  private final List<EmployeeEntity> employeeEntities;
  private List<ProductData> products;
  private List<CarrierData> carriers;
  private List<WarehouseData> warehouses;
  private List<EmployeeData> employees;

  public JpaToMsConverter(JpaDataGenerator dataGenerator) {
    if (dataGenerator.getWarehouses() == null) {
      dataGenerator.generate();
    }
    productEntities = dataGenerator.getProducts();
    carrierEntities = dataGenerator.getCarriers();
    warehouseEntities = dataGenerator.getWarehouses();
    employeeEntities = dataGenerator.getEmployees();
    products = null;
    warehouses = null;
    employees = null;
    carriers = null;
  }

  public void convert() {
    products = convertProducts(productEntities);
    carriers = convertCarriers(carrierEntities);
    warehouses = convertWarehouses(warehouseEntities, products, carriers);
    employees = convertEmployees(employeeEntities, warehouses);
  }

  @Override
  public List<ProductData> getProducts() {
    return products;
  }

  @Override
  public List<CarrierData> getCarriers() {
    return carriers;
  }

  @Override
  public List<WarehouseData> getWarehouses() {
    return warehouses;
  }

  @Override
  public List<EmployeeData> getEmployees() {
    return employees;
  }

  private void clearIds() {
    if (products == null || carriers == null || warehouses == null || employees == null) {
      throw new IllegalStateException();
    }
    products.forEach(p -> p.setId(null));
    carriers.forEach(c -> c.setId(null));
    for (WarehouseData warehouse : warehouses) {
      warehouse.setId(null);
      for (DistrictData district : warehouse.getDistricts()) {
        district.setId(null);
        for (CustomerData customer : district.getCustomers()) {
          customer.setId(null);
          customer.getPayments().forEach(p -> p.setId(null));
        }
        for (OrderData order : district.getOrders()) {
          order.setId(null);
          order.getItems().forEach(i -> i.setId(null));
        }
      }
    }
    employees.forEach(e -> e.setId(null));
  }

  private List<ProductData> convertProducts(List<ProductEntity> ps) {
    List<ProductData> products = new ArrayList<>(ps.size());
    for (ProductEntity p : ps) {
      ProductData product = new ProductData();
      product.setId(p.getId());
      product.setName(p.getName());
      product.setImagePath(p.getImagePath());
      product.setPrice(p.getPrice());
      product.setData(p.getData());
      products.add(product);
    }
    return products;
  }

  private List<CarrierData> convertCarriers(List<CarrierEntity> cs) {
    List<CarrierData> carriers = new ArrayList<>(cs.size());
    for (CarrierEntity c : cs) {
      CarrierData carrier = new CarrierData();
      carrier.setId(c.getId());
      carrier.setName(c.getName());
      carrier.setPhoneNumber(c.getPhoneNumber());
      carrier.setAddress(address(c.getAddress()));
      carriers.add(carrier);
    }
    return carriers;
  }

  private List<WarehouseData> convertWarehouses(
      List<WarehouseEntity> ws, List<ProductData> products, List<CarrierData> carriers) {
    List<WarehouseData> warehouses = new ArrayList<>(ws.size());
    for (WarehouseEntity w : ws) {
      WarehouseData warehouse = new WarehouseData();
      warehouse.setId(w.getId());
      warehouse.setName(w.getName());
      warehouse.setAddress(address(w.getAddress()));
      warehouse.setYearToDateBalance(w.getYearToDateBalance());
      warehouse.setSalesTax(w.getSalesTax());
      warehouse.setStocks(
          w.getStocks().parallelStream()
              .map(s -> stock(s, warehouse, findProductById(s.getProduct().getId(), products)))
              .collect(Collectors.toList()));
      warehouses.add(warehouse);
    }
    // Create districts and all related components, which require references to all warehouses
    for (WarehouseData w : warehouses) {
      WarehouseEntity entity = findWarehouseEntityById(w.getId(), ws);
      List<DistrictData> districts =
          entity.getDistricts().stream()
              .map(d -> district(d, w, warehouses, products, carriers))
              .collect(Collectors.toList());
      districts.forEach(d -> d.setWarehouse(w));
      w.setDistricts(districts);
    }
    return warehouses;
  }

  private List<EmployeeData> convertEmployees(
      List<EmployeeEntity> es, List<WarehouseData> warehouses) {
    List<EmployeeData> employees = new ArrayList<>(es.size());
    for (EmployeeEntity e : es) {
      EmployeeData employee = new EmployeeData();
      employee.setId(e.getId());
      employee.setAddress(address(e.getAddress()));
      employee.setFirstName(e.getFirstName());
      employee.setMiddleName(e.getMiddleName());
      employee.setLastName(e.getLastName());
      employee.setPhoneNumber(e.getPhoneNumber());
      employee.setEmail(e.getEmail());
      employee.setUsername(e.getUsername());
      employee.setPassword(e.getPassword());
      employee.setTitle(e.getTitle());
      employee.setDistrict(findDistrictById(e.getDistrict().getId(), warehouses));
      employees.add(employee);
    }
    return employees;
  }

  private DistrictData district(
      DistrictEntity d,
      WarehouseData w,
      List<WarehouseData> ws,
      List<ProductData> ps,
      List<CarrierData> cs) {
    DistrictData district = new DistrictData();
    district.setId(d.getId());
    district.setName(d.getName());
    district.setAddress(address(d.getAddress()));
    district.setSalesTax(d.getSalesTax());
    district.setYearToDateBalance(d.getYearToDateBalance());
    district.setWarehouse(w);
    district.setOrders(orders(d.getOrders(), district, ws, ps, cs));
    district.setCustomers(customers(d.getCustomers(), district));
    // Set the actual customer, as the orders(...) method only assigns a dummy object
    district
        .getOrders()
        .forEach(
            o -> {
              String customerId = o.getCustomer().getId();
              o.setCustomer(
                  district.getCustomers().stream()
                      .filter(c -> c.getId().equals(customerId))
                      .findAny()
                      .orElseThrow(IllegalStateException::new));
            });
    return district;
  }

  private static List<CustomerData> customers(List<CustomerEntity> cs, DistrictData d) {
    List<CustomerData> customers = new ArrayList<>(cs.size());
    for (CustomerEntity c : cs) {
      CustomerData customer = new CustomerData();
      customer.setId(c.getId());
      customer.setAddress(address(c.getAddress()));
      customer.setFirstName(c.getFirstName());
      customer.setMiddleName(c.getMiddleName());
      customer.setLastName(c.getLastName());
      customer.setPhoneNumber(c.getPhoneNumber());
      customer.setEmail(c.getEmail());
      customer.setCredit(c.getCredit());
      customer.setCreditLimit(c.getCreditLimit());
      customer.setBalance(c.getBalance());
      customer.setDiscount(c.getDiscount());
      customer.setData(c.getData());
      customer.setPaymentCount(c.getPaymentCount());
      customer.setYearToDatePayment(c.getYearToDatePayment());
      customer.setPaymentCount(c.getPaymentCount());
      customer.setDeliveryCount(c.getDeliveryCount());
      customer.setPayments(payments(c.getPayments(), customer, d));
      customer.setOrders(findOrdersByCustomerId(c.getId(), d.getOrders()));
      customer.getOrders().forEach(o -> o.setCustomer(customer));
      customer.setDistrict(d);
      customers.add(customer);
    }
    return customers;
  }

  private static List<OrderData> orders(
      List<OrderEntity> os,
      DistrictData d,
      List<WarehouseData> ws,
      List<ProductData> ps,
      List<CarrierData> cs) {
    return os.parallelStream()
        .map(
            o -> {
              OrderData order = new OrderData();
              order.setId(o.getId());
              order.setItemCount(o.getItemCount());
              order.setEntryDate(o.getEntryDate());
              order.setFulfilled(o.isFulfilled());
              order.setCarrier(
                  o.getCarrier() == null ? null : findCarrierById(o.getCarrier().getId(), cs));
              order.setAllLocal(o.isAllLocal());
              order.setDistrict(d);
              // Setting the actual customer must be handled by the caller!!!
              CustomerData customerDummy = new CustomerData();
              customerDummy.setId(o.getCustomer().getId());
              order.setCustomer(customerDummy);
              order.setItems(orderItems(o.getItems(), order, ws, ps));
              return order;
            })
        .sorted(Comparator.comparing(OrderData::getEntryDate))
        .collect(Collectors.toList());
  }

  private static List<OrderItemData> orderItems(
      List<OrderItemEntity> ois, OrderData o, List<WarehouseData> ws, List<ProductData> ps) {
    return ois.stream()
        .map(
            item -> {
              OrderItemData orderItem = new OrderItemData();
              orderItem.setId(item.getId());
              orderItem.setOrder(o);
              orderItem.setProduct(findProductById(item.getProduct().getId(), ps));
              orderItem.setSupplyingWarehouse(
                  findWarehouseById(item.getSupplyingWarehouse().getId(), ws));
              orderItem.setAmount(item.getAmount());
              orderItem.setQuantity(item.getQuantity());
              orderItem.setNumber(item.getNumber());
              orderItem.setDistInfo(item.getDistInfo());
              orderItem.setDeliveryDate(item.getDeliveryDate());
              return orderItem;
            })
        .collect(Collectors.toList());
  }

  private static List<PaymentData> payments(
      List<PaymentEntity> ps, CustomerData c, DistrictData d) {
    return ps.parallelStream()
        .map(
            p -> {
              PaymentData payment = new PaymentData();
              payment.setId(p.getId());
              payment.setAmount(p.getAmount());
              payment.setDate(p.getDate());
              payment.setData(p.getData());
              payment.setCustomer(c);
              payment.setDistrict(d);
              return payment;
            })
        .sorted(Comparator.comparing(PaymentData::getDate))
        .collect(Collectors.toList());
  }

  private static StockData stock(StockEntity s, WarehouseData w, ProductData p) {
    StockData stock = new StockData();
    stock.setId(s.getId());
    stock.setWarehouse(w);
    stock.setProduct(p);
    stock.setData(s.getData());
    stock.setQuantity(s.getQuantity());
    stock.setDist01(s.getDist01());
    stock.setDist02(s.getDist02());
    stock.setDist03(s.getDist03());
    stock.setDist04(s.getDist04());
    stock.setDist05(s.getDist05());
    stock.setDist06(s.getDist06());
    stock.setDist07(s.getDist07());
    stock.setDist08(s.getDist08());
    stock.setDist09(s.getDist09());
    stock.setDist10(s.getDist10());
    stock.setOrderCount(s.getOrderCount());
    stock.setRemoteCount(s.getRemoteCount());
    stock.setOrderCount(s.getOrderCount());
    return stock;
  }

  private static AddressData address(AddressEmbeddable a) {
    AddressData address = new AddressData();
    address.setStreet1(a.getStreet1());
    address.setStreet2(a.getStreet2());
    address.setCity(a.getCity());
    address.setZipCode(a.getZipCode());
    address.setState(a.getState());
    return address;
  }

  private static List<OrderData> findOrdersByCustomerId(String id, List<OrderData> orders) {
    return orders.stream()
        .filter(o -> o.getCustomer().getId().equals(id))
        .collect(Collectors.toList());
  }

  private static WarehouseEntity findWarehouseEntityById(String id, List<WarehouseEntity> ws) {
    return ws.stream()
        .filter(w -> w.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static ProductData findProductById(String id, List<ProductData> products) {
    return products.stream()
        .filter(p -> p.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static WarehouseData findWarehouseById(String id, List<WarehouseData> warehouses) {
    return warehouses.stream()
        .filter(w -> w.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static CustomerData findCustomerById(String id, List<CustomerData> customers) {
    return customers.stream()
        .filter(c -> c.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static CarrierData findCarrierById(String id, List<CarrierData> carriers) {
    return carriers.stream()
        .filter(c -> c.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static DistrictData findDistrictById(String id, List<WarehouseData> warehouses) {
    return warehouses.stream()
        .flatMap(w -> w.getDistricts().stream())
        .filter(d -> d.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }
}
