package de.uniba.dsg.jpb.data.gen.ms;

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
import de.uniba.dsg.jpb.util.Stopwatch;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Converts a JPA data model to a MicroStream data model. Converting an existing model ensures that
 * both models are alike and at the same time eliminates the need for duplicate model generation
 * code.
 *
 * @see JpaDataGenerator
 * @author Benedikt Full
 */
public class JpaToMsConverter {

  private static final Logger LOG = LogManager.getLogger(JpaToMsConverter.class);
  private final List<ProductEntity> productEntities;
  private final List<CarrierEntity> carrierEntities;
  private final List<WarehouseEntity> warehouseEntities;
  private final List<EmployeeEntity> employeeEntities;
  private List<ProductData> products;
  private List<CarrierData> carriers;
  private List<WarehouseData> warehouses;
  private List<StockData> stocks;
  private List<DistrictData> districts;
  private List<EmployeeData> employees;
  private List<CustomerData> customers;
  private List<OrderData> orders;
  private List<OrderItemData> orderItems;
  private List<PaymentData> payments;

  public JpaToMsConverter(JpaDataGenerator dataGenerator) {
    if (dataGenerator.getWarehouses() == null) {
      dataGenerator.generate();
    }
    productEntities = dataGenerator.getProducts();
    carrierEntities = dataGenerator.getCarriers();
    warehouseEntities = dataGenerator.getWarehouses();
    employeeEntities = dataGenerator.getEmployees();
    products = null;
    carriers = null;
    warehouses = null;
    stocks = null;
    districts = null;
    employees = null;
    customers = null;
    orders = null;
    payments = null;
  }

  public void convert() {
    Stopwatch stopwatch = new Stopwatch(true);
    products = convertProducts(productEntities);
    carriers = convertCarriers(carrierEntities);
    warehouses = convertWarehouses(warehouseEntities, products, carriers);
    stocks = convertStocks(warehouseEntities);
    districts = convertDistricts(warehouseEntities);
    employees = convertEmployees(employeeEntities, warehouses);
    customers = convertCustomers(warehouseEntities);
    orders = convertOrders(warehouseEntities);
    orderItems = convertOrderItems(warehouseEntities);
    payments = convertPayments(warehouseEntities);
    stopwatch.stop();
    LOG.info("Converted model data to MicroStream data, took {}", stopwatch.getDuration());
  }

  public List<ProductData> getProducts() {
    return products;
  }

  public List<CarrierData> getCarriers() {
    return carriers;
  }

  public List<WarehouseData> getWarehouses() {
    return warehouses;
  }

  public List<StockData> getStocks() {
    return stocks;
  }

  public List<DistrictData> getDistricts() {
    return districts;
  }

  public List<EmployeeData> getEmployees() {
    return employees;
  }

  public List<CustomerData> getCustomers() {
    return customers;
  }

  public List<OrderData> getOrders() {
    return orders;
  }

  public List<OrderItemData> getOrderItems() {
    return orderItems;
  }

  public List<PaymentData> getPayments() {
    return payments;
  }

  private List<ProductData> convertProducts(List<ProductEntity> ps) {
    List<ProductData> products = new ArrayList<>(ps.size());
    for (ProductEntity p : ps) {
      ProductData product = new ProductData();
      product.setId(p.getId());
      product.setImagePath(p.getImagePath());
      product.setName(p.getName());
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
      warehouse.setSalesTax(w.getSalesTax());
      warehouse.setYearToDateBalance(w.getYearToDateBalance());
      warehouses.add(warehouse);
    }
    return warehouses;
  }

  private List<StockData> convertStocks(List<WarehouseEntity> ws) {
    return ws.stream()
        .flatMap(w -> w.getStocks().stream())
        .map(JpaToMsConverter::stock)
        .collect(Collectors.toList());
  }

  private static StockData stock(StockEntity s) {
    StockData stock = new StockData();
    stock.setId(s.getId());
    stock.setWarehouseId(s.getWarehouse().getId());
    stock.setProductId(s.getProduct().getId());
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

  private List<DistrictData> convertDistricts(List<WarehouseEntity> ws) {
    List<DistrictData> districts = new ArrayList<>();
    for (WarehouseEntity w : ws) {
      for (DistrictEntity d : w.getDistricts()) {
        districts.add(district(d, w));
      }
    }
    return districts;
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
      employee.setDistrictId(e.getDistrict().getId());
      employee.setDistrictWarehouseId(e.getDistrict().getWarehouse().getId());
      employees.add(employee);
    }
    return employees;
  }

  private List<CustomerData> convertCustomers(List<WarehouseEntity> ws) {
    List<CustomerEntity> cs = new ArrayList<>();
    for (WarehouseEntity w : ws) {
      for (DistrictEntity d : w.getDistricts()) {
        cs.addAll(d.getCustomers());
      }
    }
    return customers(cs);
  }

  private static List<CustomerData> customers(List<CustomerEntity> cs) {
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
      customer.setDistrictId(c.getDistrict().getId());
      customers.add(customer);
    }
    return customers;
  }

  private List<OrderData> convertOrders(List<WarehouseEntity> ws) {
    List<OrderEntity> os = new ArrayList<>();
    for (WarehouseEntity w : ws) {
      for (DistrictEntity d : w.getDistricts()) {
        os.addAll(d.getOrders());
      }
    }
    return orders(os);
  }

  private static List<OrderData> orders(List<OrderEntity> os) {
    return os.parallelStream()
        .map(
            o -> {
              OrderData order = new OrderData();
              order.setId(o.getId());
              order.setItemCount(o.getItemCount());
              order.setEntryDate(o.getEntryDate());
              order.setFulfilled(o.isFulfilled());
              order.setCarrierId(o.getCarrier() == null ? null : o.getCarrier().getId());
              order.setAllLocal(o.isAllLocal());
              order.setDistrictId(o.getDistrict().getId());
              order.setCustomerId(o.getCustomer().getId());
              return order;
            })
        .sorted(Comparator.comparing(OrderData::getEntryDate))
        .collect(Collectors.toList());
  }

  private List<OrderItemData> convertOrderItems(List<WarehouseEntity> ws) {
    List<OrderItemEntity> ois = new ArrayList<>();
    for (WarehouseEntity w : ws) {
      for (DistrictEntity d : w.getDistricts()) {
        for (OrderEntity o : d.getOrders()) {
          ois.addAll(o.getItems());
        }
      }
    }
    return orderItems(ois);
  }

  private static List<OrderItemData> orderItems(List<OrderItemEntity> ois) {
    return ois.stream()
        .map(
            item -> {
              OrderItemData orderItem = new OrderItemData();
              orderItem.setId(item.getId());
              orderItem.setOrderId(item.getOrder().getId());
              orderItem.setProductId(item.getProduct().getId());
              orderItem.setSupplyingWarehouseId(item.getSupplyingWarehouse().getId());
              orderItem.setAmount(item.getAmount());
              orderItem.setQuantity(item.getQuantity());
              orderItem.setNumber(item.getNumber());
              orderItem.setDistInfo(item.getDistInfo());
              orderItem.setDeliveryDate(item.getDeliveryDate());
              return orderItem;
            })
        .collect(Collectors.toList());
  }

  private List<PaymentData> convertPayments(List<WarehouseEntity> ws) {
    List<PaymentEntity> ps =
        ws.parallelStream()
            .flatMap(
                w ->
                    w.getDistricts().parallelStream()
                        .flatMap(
                            d ->
                                d.getCustomers().parallelStream()
                                    .flatMap(c -> c.getPayments().stream())))
            .collect(Collectors.toList());
    return payments(ps);
  }

  private static List<PaymentData> payments(List<PaymentEntity> ps) {
    return ps.parallelStream()
        .map(
            p -> {
              PaymentData payment = new PaymentData();
              payment.setId(p.getId());
              payment.setAmount(p.getAmount());
              payment.setDate(p.getDate());
              payment.setData(p.getData());
              payment.setCustomerId(p.getCustomer().getId());
              payment.setDistrictId(p.getDistrict().getId());
              return payment;
            })
        .sorted(Comparator.comparing(PaymentData::getDate))
        .collect(Collectors.toList());
  }

  private DistrictData district(DistrictEntity d, WarehouseEntity w) {
    DistrictData district = new DistrictData();
    district.setId(d.getId());
    district.setWarehouseId(d.getWarehouse().getId());
    district.setName(d.getName());
    district.setAddress(address(d.getAddress()));
    district.setSalesTax(d.getSalesTax());
    district.setYearToDateBalance(d.getYearToDateBalance());
    return district;
  }

  private static AddressData address(AddressEmbeddable a) {
    return new AddressData(
        a.getStreet1(), a.getStreet2(), a.getZipCode(), a.getCity(), a.getState());
  }
}
