package de.uniba.dsg.test.data.gen;

import de.uniba.dsg.wss.data.gen.Configuration;
import de.uniba.dsg.wss.data.gen.IDataGenerator;
import de.uniba.dsg.wss.data.gen.Stats;
import de.uniba.dsg.wss.data.gen.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestDataGenerator implements IDataGenerator {

  protected static final int WAREHOUSES = 5;
  protected static final int PRODUCTS = 10;
  protected static final int DISTRICTS = 2 * WAREHOUSES;
  protected static final int CUSTOMERS = 2 * DISTRICTS;

  private List<Warehouse> warehouses;
  private List<Employee> employees;
  private List<Product> products;
  private List<Carrier> carriers;

  public TestDataGenerator() {

    Map<String, Warehouse> warehouses = new HashMap<>();
    for (int i = 0; i < WAREHOUSES; i++) {
      String id = "W" + i;
      Warehouse w = new Warehouse();
      w.setId(id);
      w.setAddress(new Address());
      w.setDistricts(new ArrayList<>());
      w.setStocks(new ArrayList<>());
      w.setSalesTax(0.1);
      warehouses.put(id, w);
    }

    Map<String, Product> products = new HashMap<>();
    for (int i = 0; i < PRODUCTS; i++) {
      String id = "P" + i;
      Product p = new Product();
      p.setId(id);
      p.setData(id + "-data");
      p.setName("");
      p.setImagePath("");
      p.setPrice(12.99 + i);
      products.put(id, p);
    }

    Map<String, Stock> stocks = new HashMap<>();
    for (int i = 0; i < WAREHOUSES; i++) {
      for (int j = 0; j < PRODUCTS; j++) {
        if ((i + j) % 2 == 0) {
          String stockId = "W" + i + "P" + j;
          Warehouse warehouse = warehouses.get("W" + i);
          Product product = products.get("P" + j);
          Stock stock = new Stock();
          stock.setId(stockId);
          stock.setWarehouse(warehouse);
          warehouse.getStocks().add(stock);
          stock.setProduct(product);
          stock.setQuantity(i + j);
          // omit other values
          stocks.put(stock.getId(), stock);
        }
      }
    }

    Map<String, District> districts = new HashMap<>();
    for (int i = 0; i < DISTRICTS; i++) {
      String districtId = "D" + i;
      Warehouse warehouse = warehouses.get("W" + (i % WAREHOUSES));
      District district = new District();
      district.setId(districtId);
      district.setWarehouse(warehouse);
      district.setSalesTax(1.19);
      district.setCustomers(new ArrayList<>());
      district.setOrders(new ArrayList<>());
      district.setAddress(new Address());

      warehouse.getDistricts().add(district);
      districts.put(districtId, district);
    }

    Map<String, Customer> customers = new HashMap<>();
    for (int i = 0; i < CUSTOMERS; i++) {
      String customerId = "C" + i;
      District district = districts.get("D" + (i % DISTRICTS));
      Customer customer = new Customer();
      customer.setId(customerId);
      customer.setDistrict(district);
      customer.setPayments(new ArrayList<>());
      customer.setOrders(new ArrayList<>());
      // omit other values
      Payment p = new Payment();
      p.setId("P" + i);
      p.setCustomer(customer);
      customer.getPayments().add(p);
      customer.setAddress(new Address());

      customers.put(customerId, customer);
      district.getCustomers().add(customer);
    }

    Map<String, Order> orders = new HashMap<>();
    for (int i = 0; i < 2 * DISTRICTS; i++) {
      Order order = new Order();
      order.setId("O" + i);
      order.setDistrict(districts.get("D" + (i % DISTRICTS)));
      order.setCustomer(customers.get("C" + (i % DISTRICTS)));
      order.setItems(new ArrayList<>());
      order.setEntryDate(LocalDateTime.now());
      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setProduct(products.get("P" + (i % PRODUCTS)));
      item.setSupplyingWarehouse(warehouses.get("W0"));
      item.setNumber(1);
      item.setQuantity(2);
      order.getItems().add(item);
      districts.get("D" + (i % DISTRICTS)).getOrders().add(order);
      customers.get("C" + (i % DISTRICTS)).getOrders().add(order);
      orders.put(order.getId(), order);
    }

    Carrier carrier = new Carrier();
    carrier.setId("CC0");
    carrier.setName("DHL");
    carrier.setAddress(new Address());

    Employee employee = new Employee();
    employee.setId("E0");
    employee.setDistrict(districts.get("D0"));
    employee.setPassword("jpb");
    employee.setUsername("jpb");
    employee.setAddress(new Address());

    this.warehouses =
        warehouses.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
    this.employees = List.of(employee);
    this.products =
        products.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
    this.carriers = List.of(carrier);
  }

  @Override
  public List<Warehouse> getWarehouses() {
    return this.warehouses;
  }

  @Override
  public List<Employee> getEmployees() {
    return this.employees;
  }

  @Override
  public List<Product> getProducts() {
    return this.products;
  }

  @Override
  public List<Carrier> getCarriers() {
    return this.carriers;
  }

  @Override
  public boolean isDataGenerated() {
    return true;
  }

  @Override
  public Stats generate() {
    return new Stats();
  }

  @Override
  public Configuration getConfiguration() {
    return new Configuration();
  }
}
