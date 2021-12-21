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
 * @author Benedikt Full
 * @author Johannes Manner
 */
public class JpaDataConverter
    implements DataConverter<ProductEntity, WarehouseEntity, EmployeeEntity, CarrierEntity> {

  private static final Logger LOG = LogManager.getLogger(JpaDataConverter.class);

  public JpaDataConverter() {}

  @Override
  public JpaDataModel convert(DataModel<Product, Warehouse, Employee, Carrier> model) {
    // Create model objects by converting provided template
    Stopwatch stopwatch = new Stopwatch().start();
    Map<String, ProductEntity> products = convertProducts(model.getProducts());
    Map<String, CarrierEntity> carriers = convertCarriers(model.getCarriers());
    Map<String, WarehouseEntity> warehouses = convertWarehouses(model.getWarehouses());
    List<StockEntity> stocks = convertStocks(model.getWarehouses(), warehouses, products);
    Map<String, DistrictEntity> districts = convertDistricts(model.getWarehouses(), warehouses);
    List<EmployeeEntity> employees = convertEmployees(model.getEmployees(), districts);
    Map<String, CustomerEntity> customers = convertCustomers(model.getWarehouses(), districts);
    Map<String, OrderEntity> orders =
        convertOrders(model.getWarehouses(), carriers, districts, customers);
    convertOrderItems(model.getWarehouses(), products, warehouses, orders);
    convertPayments(model.getWarehouses(), districts, customers);
    stopwatch.stop();

    // Create summary data
    Stats stats = new Stats();
    stats.setTotalModelObjectCount(model.getStats().getTotalModelObjectCount());
    stats.setDurationMillis(stopwatch.getDurationMillis());
    stats.setDuration(stopwatch.getDuration());

    // Wrap everything in model instance
    JpaDataModel generatedModel =
        new JpaDataModel(
            new ArrayList<>(products.values()),
            new ArrayList<>(warehouses.values()),
            employees,
            new ArrayList<>(carriers.values()),
            stats);

    LOG.info("Converted model data to JPA entity data, took {}", stopwatch.getDuration());

    return generatedModel;
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

  private List<StockEntity> convertStocks(
      List<Warehouse> ws,
      Map<String, WarehouseEntity> warehouses,
      Map<String, ProductEntity> products) {
    List<StockEntity> stocks =
        ws.stream()
            .flatMap(w -> w.getStocks().stream())
            .map(s -> stock(s, warehouses, products))
            .collect(Collectors.toList());
    LOG.debug("Converted {} stocks", stocks.size());
    return stocks;
  }

  private StockEntity stock(
      Stock s, Map<String, WarehouseEntity> warehouses, Map<String, ProductEntity> products) {
    StockEntity stock = new StockEntity();
    stock.setId(s.getId());
    stock.setProduct(products.get(s.getProduct().getId()));
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
    WarehouseEntity warehouseEntity = warehouses.get(s.getWarehouse().getId());
    stock.setWarehouse(warehouseEntity);
    warehouseEntity.getStocks().add(stock);

    return stock;
  }

  private Map<String, DistrictEntity> convertDistricts(
      List<Warehouse> ws, Map<String, WarehouseEntity> warehouses) {
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
        WarehouseEntity warehouse = warehouses.get(d.getWarehouse().getId());
        district.setWarehouse(warehouse);
        warehouse.getDistricts().add(district);

        districts.put(district.getId(), district);
      }
    }
    LOG.debug("Converted {} districts", districts.size());
    return districts;
  }

  private List<EmployeeEntity> convertEmployees(
      List<Employee> es, Map<String, DistrictEntity> districts) {
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
      employee.setRole(e.getRole());
      employee.setTitle(e.getTitle());
      employee.setDistrict(districts.get(e.getDistrict().getId()));
      employees.add(employee);
    }
    LOG.debug("Converted {} employees", employees.size());
    return employees;
  }

  private Map<String, CustomerEntity> convertCustomers(
      List<Warehouse> ws, Map<String, DistrictEntity> districts) {
    List<Customer> cs = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        cs.addAll(d.getCustomers());
      }
    }
    return customers(cs, districts);
  }

  private Map<String, CustomerEntity> customers(
      List<Customer> cs, Map<String, DistrictEntity> districts) {
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

      // referential integrity
      DistrictEntity district = districts.get(c.getDistrict().getId());
      customer.setDistrict(district);
      district.getCustomers().add(customer);

      customers.put(customer.getId(), customer);
    }
    LOG.debug("Converted {} customers", customers.size());
    return customers;
  }

  private Map<String, OrderEntity> convertOrders(
      List<Warehouse> ws,
      Map<String, CarrierEntity> carriers,
      Map<String, DistrictEntity> districts,
      Map<String, CustomerEntity> customers) {
    List<Order> os = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        os.addAll(d.getOrders());
      }
    }
    return orders(os, carriers, districts, customers);
  }

  private Map<String, OrderEntity> orders(
      List<Order> os,
      Map<String, CarrierEntity> carriers,
      Map<String, DistrictEntity> districts,
      Map<String, CustomerEntity> customers) {
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
                    order.setCarrier(carriers.get(o.getCarrier().getId()));
                  }
                  order.setAllLocal(o.isAllLocal());

                  // referential integrity
                  DistrictEntity districtEntity = districts.get(o.getDistrict().getId());
                  order.setDistrict(districtEntity);
                  districtEntity.getOrders().add(order);

                  // referential integrity
                  CustomerEntity customerEntity = customers.get(o.getCustomer().getId());
                  order.setCustomer(customerEntity);
                  customerEntity.getOrders().add(order);

                  return order;
                })
            .sorted(Comparator.comparing(OrderEntity::getEntryDate))
            .collect(Collectors.toList());

    Map<String, OrderEntity> orderMap = new HashMap<>();
    for (OrderEntity orderEntity : orders) {
      orderMap.put(orderEntity.getId(), orderEntity);
    }
    LOG.debug("Converted {} orders", orders.size());
    return orderMap;
  }

  private List<OrderItemEntity> convertOrderItems(
      List<Warehouse> ws,
      Map<String, ProductEntity> products,
      Map<String, WarehouseEntity> warehouses,
      Map<String, OrderEntity> orders) {
    List<OrderItem> ois = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        for (Order o : d.getOrders()) {
          ois.addAll(o.getItems());
        }
      }
    }
    return orderItems(ois, products, warehouses, orders);
  }

  private List<OrderItemEntity> orderItems(
      List<OrderItem> ois,
      Map<String, ProductEntity> products,
      Map<String, WarehouseEntity> warehouses,
      Map<String, OrderEntity> orders) {
    List<OrderItemEntity> orderItems =
        ois.stream()
            .map(
                item -> {
                  OrderItemEntity orderItem = new OrderItemEntity();
                  orderItem.setId(item.getId());
                  orderItem.setProduct(products.get(item.getProduct().getId()));
                  orderItem.setSupplyingWarehouse(
                      warehouses.get(item.getSupplyingWarehouse().getId()));
                  orderItem.setAmount(item.getAmount());
                  orderItem.setQuantity(item.getQuantity());
                  orderItem.setNumber(item.getNumber());
                  orderItem.setDistInfo(item.getDistInfo());
                  orderItem.setDeliveryDate(item.getDeliveryDate());

                  // referential integrity
                  OrderEntity orderEntity = orders.get(item.getOrder().getId());
                  orderItem.setOrder(orderEntity);
                  orderEntity.getItems().add(orderItem);

                  return orderItem;
                })
            .collect(Collectors.toList());
    LOG.debug("Converted {} order items", orderItems.size());
    return orderItems;
  }

  private List<PaymentEntity> convertPayments(
      List<Warehouse> ws,
      Map<String, DistrictEntity> districts,
      Map<String, CustomerEntity> customers) {
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
    return payments(ps, districts, customers);
  }

  private List<PaymentEntity> payments(
      List<Payment> ps,
      Map<String, DistrictEntity> districts,
      Map<String, CustomerEntity> customers) {
    List<PaymentEntity> payments =
        ps.parallelStream()
            .map(
                p -> {
                  PaymentEntity payment = new PaymentEntity();
                  payment.setId(p.getId());
                  payment.setAmount(p.getAmount());
                  payment.setDate(p.getDate());
                  payment.setData(p.getData());
                  payment.setDistrict(districts.get(p.getDistrict().getId()));

                  // referential integrity
                  CustomerEntity customer = customers.get(p.getCustomer().getId());
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
