package de.uniba.dsg.wss.data.gen;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data generator for generating the same, small set of test data.
 *
 * @author Johannes Manner
 * @author Benedikt Full
 */
public class TestDataGenerator implements DataGenerator {

  protected static final int WAREHOUSES = 5;
  protected static final int PRODUCTS = 10;
  protected static final int DISTRICTS = 2 * WAREHOUSES;
  protected static final int CUSTOMERS = 2 * DISTRICTS;

  public TestDataGenerator() {}

  @Override
  public DataModel<Product, Warehouse, Employee, Carrier> generate() {
    Map<String, Warehouse> warehouses = new HashMap<>();
    for (int i = 0; i < WAREHOUSES; i++) {
      String id = "W" + i;
      Warehouse w = new Warehouse();
      w.setId(id);
      w.setAddress(new Address());
      w.setDistricts(new ArrayList<>());
      w.setStocks(new ArrayList<>());
      w.setSalesTax(0.1);
      w.setName("");
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
          stock.setData("");
          stock.setDist01("");
          stock.setDist02("");
          stock.setDist03("");
          stock.setDist04("");
          stock.setDist05("");
          stock.setDist06("");
          stock.setDist07("");
          stock.setDist08("");
          stock.setDist09("");
          stock.setDist10("");
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
      district.setName("");

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
      customer.setEmail(customerId + "@jbp.io");
      customer.setFirstName(customerId + "-first");
      customer.setMiddleName(customerId + "-middle");
      customer.setLastName(customerId + "-last");
      customer.setPhoneNumber("");
      customer.setData("");
      customer.setSince(LocalDateTime.now());
      customer.setCredit("");

      Payment p = new Payment();
      p.setId("P" + i);
      p.setCustomer(customer);
      p.setDistrict(customer.getDistrict());
      p.setData("");
      p.setDate(LocalDateTime.now());
      customer.getPayments().add(p);
      customer.setPaymentCount(1);
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
      order.setEntryDate(LocalDateTime.now().plusSeconds(i));
      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setProduct(products.get("P" + (i % PRODUCTS)));
      item.setSupplyingWarehouse(warehouses.get("W0"));
      item.setNumber(1);
      item.setQuantity(2);
      item.setDistInfo("");
      order.getItems().add(item);
      order.setItemCount(1);
      districts.get("D" + (i % DISTRICTS)).getOrders().add(order);
      customers.get("C" + (i % DISTRICTS)).getOrders().add(order);
      orders.put(order.getId(), order);
    }

    Carrier carrier = new Carrier();
    carrier.setId("CC0");
    carrier.setName("DHL");
    carrier.setAddress(new Address());
    carrier.setPhoneNumber("");

    Employee employee = new Employee();
    employee.setId("E0");
    employee.setDistrict(districts.get("D0"));
    employee.setPassword("jpb");
    employee.setUsername("jpb");
    employee.setAddress(new Address());

    return new DataGeneratorModel(
        new ArrayList<>(products.values()),
        new ArrayList<>(warehouses.values()),
        List.of(employee),
        List.of(carrier),
        new Stats());
  }
}
