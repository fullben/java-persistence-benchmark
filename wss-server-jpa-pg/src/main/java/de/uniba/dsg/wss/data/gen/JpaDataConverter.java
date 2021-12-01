package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.gen.model.Address;
import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Customer;
import de.uniba.dsg.wss.data.gen.model.District;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Order;
import de.uniba.dsg.wss.data.gen.model.OrderItem;
import de.uniba.dsg.wss.data.gen.model.Payment;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Stock;
import de.uniba.dsg.wss.data.gen.model.Warehouse;
import de.uniba.dsg.wss.data.model.AddressEmbeddable;
import de.uniba.dsg.wss.data.model.CarrierEntity;
import de.uniba.dsg.wss.data.model.CustomerEntity;
import de.uniba.dsg.wss.data.model.DistrictEntity;
import de.uniba.dsg.wss.data.model.EmployeeEntity;
import de.uniba.dsg.wss.data.model.OrderEntity;
import de.uniba.dsg.wss.data.model.OrderItemEntity;
import de.uniba.dsg.wss.data.model.PaymentEntity;
import de.uniba.dsg.wss.data.model.ProductEntity;
import de.uniba.dsg.wss.data.model.StockEntity;
import de.uniba.dsg.wss.data.model.WarehouseEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Converts a generated data model to a JPA entity data model.
 *
 * @see DataGenerator
 * @author Benedikt Full
 */
public class JpaDataConverter implements DataConverter {

  private static final Logger LOG = LogManager.getLogger(JpaDataConverter.class);
  private Map<String, ProductEntity> products;
  private Map<String, CarrierEntity> carriers;
  private Map<String, WarehouseEntity> warehouses;
  private List<StockEntity> stocks;
  private Map<String, DistrictEntity> districts;
  private List<EmployeeEntity> employees;
  private Map<String, CustomerEntity> customers;
  private Map<String, OrderEntity> orders;
  private List<OrderItemEntity> orderItems;
  private List<PaymentEntity> payments;

  public JpaDataConverter() {
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

  @Override
  public void convert(IDataGenerator generator) {
    Stopwatch stopwatch = new Stopwatch(true);
    if (!generator.isDataGenerated()) {
      generator.generate();
    }
    products = convertProducts(generator.getProducts());
    carriers = convertCarriers(generator.getCarriers());
    warehouses = convertWarehouses(generator.getWarehouses());
    stocks = convertStocks(generator.getWarehouses());
    districts = convertDistricts(generator.getWarehouses());
    employees = convertEmployees(generator.getEmployees());
    customers = convertCustomers(generator.getWarehouses());
    orders = convertOrders(generator.getWarehouses());
    orderItems = convertOrderItems(generator.getWarehouses());
    payments = convertPayments(generator.getWarehouses());
    stopwatch.stop();
    LOG.info("Converted model data to JPA entity data, took {}", stopwatch.getDuration());
  }

  @Override
  public boolean hasConvertedData() {
    return products != null
        && carriers != null
        && warehouses != null
        && stocks != null
        && districts != null
        && employees != null
        && customers != null
        && orders != null
        && orderItems != null
        && payments != null;
  }

  @Override
  public void clear() {
    if (!hasConvertedData()) {
      return;
    }
    products = null;
    carriers = null;
    warehouses = null;
    stocks = null;
    districts = null;
    employees = null;
    customers = null;
    orders = null;
    orderItems = null;
    payments = null;
  }

  public List<ProductEntity> getProducts() {
    return products.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
  }

  public List<CarrierEntity> getCarriers() {
    return carriers.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
  }

  public List<WarehouseEntity> getWarehouses() {
    return warehouses.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
  }

  public List<EmployeeEntity> getEmployees() {
    return employees;
  }


  private Map<String, ProductEntity> convertProducts(List<Product> ps) {
    Map<String, ProductEntity> products = new HashMap<>();
    for (Product p : ps) {
      ProductEntity product = new ProductEntity();
      product.setId(p.getId());
      product.setImagePath(p.getImagePath());
      product.setName(p.getName());
      product.setPrice(p.getPrice());
      product.setData(p.getData());

      products.put(product.getId(), product);
    }
    LOG.debug("Converted {} products", products.size());
    return products;
  }

  private Map<String, CarrierEntity> convertCarriers(List<Carrier> cs) {
    Map<String, CarrierEntity> carriers = new HashMap<>();
    for (Carrier c : cs) {
      CarrierEntity carrier = new CarrierEntity();
      carrier.setId(c.getId());
      carrier.setName(c.getName());
      carrier.setPhoneNumber(c.getPhoneNumber());
      carrier.setAddress(address(c.getAddress()));

      carriers.put(carrier.getId(), carrier);
    }
    LOG.debug("Converted {} carriers", carriers.size());
    return carriers;
  }

  private Map<String, WarehouseEntity> convertWarehouses(List<Warehouse> ws) {
    Map<String, WarehouseEntity> warehouses = new HashMap<>();
    for (Warehouse w : ws) {
      WarehouseEntity warehouse = new WarehouseEntity();
      warehouse.setId(w.getId());
      warehouse.setName(w.getName());
      warehouse.setAddress(address(w.getAddress()));
      warehouse.setSalesTax(w.getSalesTax());
      warehouse.setYearToDateBalance(w.getYearToDateBalance());

      warehouses.put(warehouse.getId(), warehouse);
    }
    LOG.debug("Converted {} warehouses", warehouses.size());
    return warehouses;
  }

  private List<StockEntity> convertStocks(List<Warehouse> ws) {
    List<StockEntity> stocks =
        ws.stream()
            .flatMap(w -> w.getStocks().stream())
            .map(s -> this.stock(s))
            .collect(Collectors.toList());
    LOG.debug("Converted {} stocks", stocks.size());
    return stocks;
  }

  private StockEntity stock(Stock s) {
    StockEntity stock = new StockEntity();
    stock.setId(s.getId());
    stock.setProduct(this.products.get(s.getProduct().getId()));
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

    // referential integrity
    WarehouseEntity warehouseEntity = this.warehouses.get(s.getWarehouse().getId());
    stock.setWarehouse(warehouseEntity);
    warehouseEntity.getStocks().add(stock);

    return stock;
  }

  private Map<String, DistrictEntity> convertDistricts(List<Warehouse> ws) {
    Map<String, DistrictEntity> districts = new HashMap<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {

        DistrictEntity district = new DistrictEntity();
        district.setId(d.getId());
        district.setName(d.getName());
        district.setAddress(address(d.getAddress()));
        district.setSalesTax(d.getSalesTax());
        district.setYearToDateBalance(d.getYearToDateBalance());

        // referential integrity
        WarehouseEntity warehouse = this.warehouses.get(d.getWarehouse().getId());
        district.setWarehouse(warehouse);
        warehouse.getDistricts().add(district);

        districts.put(district.getId(), district);
      }
    }
    LOG.debug("Converted {} districts", districts.size());
    return districts;
  }

  private List<EmployeeEntity> convertEmployees(List<Employee> es) {
    List<EmployeeEntity> employees = new ArrayList<>(es.size());
    for (Employee e : es) {
      EmployeeEntity employee = new EmployeeEntity();
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
      employee.setDistrict(this.districts.get(e.getDistrict().getId()));
      employees.add(employee);
    }
    LOG.debug("Converted {} employees", employees.size());
    return employees;
  }

  private Map<String, CustomerEntity> convertCustomers(List<Warehouse> ws) {
    List<Customer> cs = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        cs.addAll(d.getCustomers());
      }
    }
    return customers(cs);
  }

  private Map<String, CustomerEntity> customers(List<Customer> cs) {
    Map<String, CustomerEntity> customers = new HashMap<>();
    for (Customer c : cs) {
      CustomerEntity customer = new CustomerEntity();
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
      customer.setSince(LocalDateTime.now());

      //referential integrity
      DistrictEntity district = this.districts.get(c.getDistrict().getId());
      customer.setDistrict(district);
      district.getCustomers().add(customer);

      customers.put(customer.getId(), customer);
    }
    LOG.debug("Converted {} customers", customers.size());
    return customers;
  }

  private Map<String, OrderEntity> convertOrders(List<Warehouse> ws) {
    List<Order> os = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        os.addAll(d.getOrders());
      }
    }
    return orders(os);
  }

  private Map<String, OrderEntity> orders(List<Order> os) {
    List<OrderEntity> orders =
        os.parallelStream()
            .map(
                o -> {
                  OrderEntity order = new OrderEntity();
                  order.setId(o.getId());
                  order.setItemCount(o.getItemCount());
                  order.setEntryDate(o.getEntryDate());
                  order.setFulfilled(o.isFulfilled());
                  if (o.getCarrier() != null) {
                    order.setCarrier(this.carriers.get(o.getCarrier().getId()));
                  }
                  order.setAllLocal(o.isAllLocal());

                  // referential integrity
                  DistrictEntity districtEntity = this.districts.get(o.getDistrict().getId());
                  order.setDistrict(districtEntity);
                  districtEntity.getOrders().add(order);

                  // referential integrity
                  CustomerEntity customerEntity = this.customers.get(o.getCustomer().getId());
                  order.setCustomer(customerEntity);
                  customerEntity.getOrders().add(order);

                  return order;
                })
            .sorted(Comparator.comparing(OrderEntity::getEntryDate))
            .collect(Collectors.toList());

    Map<String, OrderEntity> orderMap = new HashMap<>();
    for(OrderEntity orderEntity : orders) {
      orderMap.put(orderEntity.getId(), orderEntity);
    }
    LOG.debug("Converted {} orders", orders.size());
    return orderMap;
  }

  private List<OrderItemEntity> convertOrderItems(List<Warehouse> ws) {
    List<OrderItem> ois = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        for (Order o : d.getOrders()) {
          ois.addAll(o.getItems());
        }
      }
    }
    return orderItems(ois);
  }

  private List<OrderItemEntity> orderItems(List<OrderItem> ois) {
    List<OrderItemEntity> orderItems =
        ois.stream()
            .map(
                item -> {
                  OrderItemEntity orderItem = new OrderItemEntity();
                  orderItem.setId(item.getId());
                  orderItem.setProduct(this.products.get(item.getProduct().getId()));
                  orderItem.setSupplyingWarehouse(this.warehouses.get(item.getSupplyingWarehouse().getId()));
                  orderItem.setAmount(item.getAmount());
                  orderItem.setQuantity(item.getQuantity());
                  orderItem.setNumber(item.getNumber());
                  orderItem.setDistInfo(item.getDistInfo());
                  orderItem.setDeliveryDate(item.getDeliveryDate());

                  // referential integrity
                  OrderEntity orderEntity = this.orders.get(item.getOrder().getId());
                  orderItem.setOrder(orderEntity);
                  orderEntity.getItems().add(orderItem);

                  return orderItem;
                })
            .collect(Collectors.toList());
    LOG.debug("Converted {} order items", orderItems.size());
    return orderItems;
  }

  private List<PaymentEntity> convertPayments(List<Warehouse> ws) {
    List<Payment> ps =
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

  private List<PaymentEntity> payments(List<Payment> ps) {
    List<PaymentEntity> payments =
        ps.parallelStream()
            .map(
                p -> {
                  PaymentEntity payment = new PaymentEntity();
                  payment.setId(p.getId());
                  payment.setAmount(p.getAmount());
                  payment.setDate(p.getDate());
                  payment.setData(p.getData());
                  payment.setDistrict(this.districts.get(p.getDistrict().getId()));

                  // referential integrity
                  CustomerEntity customer = this.customers.get(p.getCustomer().getId());
                  payment.setCustomer(customer);
                  customer.getPayments().add(payment);

                  return payment;
                })
            .sorted(Comparator.comparing(PaymentEntity::getDate))
            .collect(Collectors.toList());
    LOG.debug("Converted {} payments", payments.size());
    return payments;
  }

  private static AddressEmbeddable address(Address a) {
    AddressEmbeddable address = new AddressEmbeddable();
    address.setStreet1(a.getStreet1());
    address.setStreet2(a.getStreet2());
    address.setZipCode(a.getZipCode());
    address.setCity(a.getCity());
    address.setState(a.getState());
    return address;
  }
}
