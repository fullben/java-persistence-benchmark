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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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
  private List<ProductEntity> products;
  private List<CarrierEntity> carriers;
  private List<WarehouseEntity> warehouses;
  private List<StockEntity> stocks;
  private List<DistrictEntity> districts;
  private List<EmployeeEntity> employees;
  private List<CustomerEntity> customers;
  private List<OrderEntity> orders;
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
  public void convert(DataGenerator generator) {
    Stopwatch stopwatch = new Stopwatch(true);
    if (!generator.isDataGenerated()) {
      generator.generate();
    }
    products = convertProducts(generator.getProducts());
    carriers = convertCarriers(generator.getCarriers());
    warehouses = convertWarehouses(generator.getWarehouses(), products, carriers);
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
    return products;
  }

  public List<CarrierEntity> getCarriers() {
    return carriers;
  }

  public List<WarehouseEntity> getWarehouses() {
    return warehouses;
  }

  public List<StockEntity> getStocks() {
    return stocks;
  }

  public List<DistrictEntity> getDistricts() {
    return districts;
  }

  public List<EmployeeEntity> getEmployees() {
    return employees;
  }

  public List<CustomerEntity> getCustomers() {
    return customers;
  }

  public List<OrderEntity> getOrders() {
    return orders;
  }

  public List<OrderItemEntity> getOrderItems() {
    return orderItems;
  }

  public List<PaymentEntity> getPayments() {
    return payments;
  }

  private List<ProductEntity> convertProducts(List<Product> ps) {
    List<ProductEntity> products = new ArrayList<>(ps.size());
    for (Product p : ps) {
      ProductEntity product = new ProductEntity();
      product.setId(p.getId());
      product.setImagePath(p.getImagePath());
      product.setName(p.getName());
      product.setPrice(p.getPrice());
      product.setData(p.getData());
      products.add(product);
    }
    LOG.debug("Converted {} products", products.size());
    return products;
  }

  private List<CarrierEntity> convertCarriers(List<Carrier> cs) {
    List<CarrierEntity> carriers = new ArrayList<>(cs.size());
    for (Carrier c : cs) {
      CarrierEntity carrier = new CarrierEntity();
      carrier.setId(c.getId());
      carrier.setName(c.getName());
      carrier.setPhoneNumber(c.getPhoneNumber());
      carrier.setAddress(address(c.getAddress()));
      carriers.add(carrier);
    }
    LOG.debug("Converted {} carriers", carriers.size());
    return carriers;
  }

  private List<WarehouseEntity> convertWarehouses(
      List<Warehouse> ws, List<ProductEntity> products, List<CarrierEntity> carriers) {
    List<WarehouseEntity> warehouses = new ArrayList<>(ws.size());
    for (Warehouse w : ws) {
      WarehouseEntity warehouse = new WarehouseEntity();
      warehouse.setId(w.getId());
      warehouse.setName(w.getName());
      warehouse.setAddress(address(w.getAddress()));
      warehouse.setSalesTax(w.getSalesTax());
      warehouse.setYearToDateBalance(w.getYearToDateBalance());
      warehouses.add(warehouse);
    }
    LOG.debug("Converted {} warehouses", warehouses.size());
    return warehouses;
  }

  private List<StockEntity> convertStocks(List<Warehouse> ws) {
    List<StockEntity> stocks =
        ws.stream()
            .flatMap(w -> w.getStocks().stream())
            .map(JpaDataConverter::stock)
            .collect(Collectors.toList());
    LOG.debug("Converted {} stocks", stocks.size());
    return stocks;
  }

  private static StockEntity stock(Stock s) {
    StockEntity stock = new StockEntity();
    stock.setId(s.getId());
    WarehouseEntity warehouse = new WarehouseEntity();
    warehouse.setId(s.getWarehouse().getId());
    stock.setWarehouse(warehouse);
    ProductEntity product = new ProductEntity();
    product.setId(s.getProduct().getId());
    stock.setProduct(product);
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

  private List<DistrictEntity> convertDistricts(List<Warehouse> ws) {
    List<DistrictEntity> districts = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        districts.add(district(d));
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
      employee.setDistrict(findDistrictById(e.getDistrict().getId(), districts));
      employees.add(employee);
    }
    LOG.debug("Converted {} employees", employees.size());
    return employees;
  }

  private List<CustomerEntity> convertCustomers(List<Warehouse> ws) {
    List<Customer> cs = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        cs.addAll(d.getCustomers());
      }
    }
    return customers(cs);
  }

  private static List<CustomerEntity> customers(List<Customer> cs) {
    List<CustomerEntity> customers = new ArrayList<>(cs.size());
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
      DistrictEntity district = new DistrictEntity();
      district.setId(c.getDistrict().getId());
      customer.setDistrict(district);
      customers.add(customer);
    }
    LOG.debug("Converted {} customers", customers.size());
    return customers;
  }

  private List<OrderEntity> convertOrders(List<Warehouse> ws) {
    List<Order> os = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        os.addAll(d.getOrders());
      }
    }
    return orders(os);
  }

  private static List<OrderEntity> orders(List<Order> os) {
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
                    CarrierEntity carrier = new CarrierEntity();
                    carrier.setId(o.getCarrier().getId());
                    order.setCarrier(carrier);
                  }
                  order.setAllLocal(o.isAllLocal());
                  DistrictEntity district = new DistrictEntity();
                  district.setId(o.getDistrict().getId());
                  order.setDistrict(district);
                  CustomerEntity customer = new CustomerEntity();
                  customer.setId(o.getCustomer().getId());
                  order.setCustomer(customer);
                  return order;
                })
            .sorted(Comparator.comparing(OrderEntity::getEntryDate))
            .collect(Collectors.toList());
    LOG.debug("Converted {} orders", orders.size());
    return orders;
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

  private static List<OrderItemEntity> orderItems(List<OrderItem> ois) {
    List<OrderItemEntity> orderItems =
        ois.stream()
            .map(
                item -> {
                  OrderItemEntity orderItem = new OrderItemEntity();
                  orderItem.setId(item.getId());
                  OrderEntity order = new OrderEntity();
                  order.setId(item.getOrder().getId());
                  orderItem.setOrder(order);
                  ProductEntity product = new ProductEntity();
                  product.setId(item.getProduct().getId());
                  orderItem.setProduct(product);
                  WarehouseEntity supplyingWarehouse = new WarehouseEntity();
                  supplyingWarehouse.setId(item.getSupplyingWarehouse().getId());
                  orderItem.setSupplyingWarehouse(supplyingWarehouse);
                  orderItem.setAmount(item.getAmount());
                  orderItem.setQuantity(item.getQuantity());
                  orderItem.setNumber(item.getNumber());
                  orderItem.setDistInfo(item.getDistInfo());
                  orderItem.setDeliveryDate(item.getDeliveryDate());
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

  private static List<PaymentEntity> payments(List<Payment> ps) {
    List<PaymentEntity> payments =
        ps.parallelStream()
            .map(
                p -> {
                  PaymentEntity payment = new PaymentEntity();
                  payment.setId(p.getId());
                  payment.setAmount(p.getAmount());
                  payment.setDate(p.getDate());
                  payment.setData(p.getData());
                  CustomerEntity customer = new CustomerEntity();
                  customer.setId(p.getCustomer().getId());
                  payment.setCustomer(customer);
                  DistrictEntity district = new DistrictEntity();
                  district.setId(p.getDistrict().getId());
                  payment.setDistrict(district);
                  return payment;
                })
            .sorted(Comparator.comparing(PaymentEntity::getDate))
            .collect(Collectors.toList());
    LOG.debug("Converted {} payments", payments.size());
    return payments;
  }

  private DistrictEntity district(District d) {
    DistrictEntity district = new DistrictEntity();
    district.setId(d.getId());
    district.setWarehouse(findWarehouseById(d.getWarehouse().getId(), warehouses));
    district.setName(d.getName());
    district.setAddress(address(d.getAddress()));
    district.setSalesTax(d.getSalesTax());
    district.setYearToDateBalance(d.getYearToDateBalance());
    return district;
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

  public WarehouseEntity findWarehouseById(String id, Collection<WarehouseEntity> entities) {
    return entities.stream()
        .filter(w -> w.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalStateException::new);
  }

  public DistrictEntity findDistrictById(String id, Collection<DistrictEntity> entities) {
    return entities.stream()
        .filter(d -> d.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalStateException::new);
  }
}
