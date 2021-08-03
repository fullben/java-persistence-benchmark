package de.uniba.dsg.jpb.server.datagen;

import com.github.javafaker.Faker;
import de.uniba.dsg.jpb.server.model.Address;
import de.uniba.dsg.jpb.server.model.Customer;
import de.uniba.dsg.jpb.server.model.District;
import de.uniba.dsg.jpb.server.model.History;
import de.uniba.dsg.jpb.server.model.Item;
import de.uniba.dsg.jpb.server.model.NewOrder;
import de.uniba.dsg.jpb.server.model.Order;
import de.uniba.dsg.jpb.server.model.OrderLine;
import de.uniba.dsg.jpb.server.model.Stock;
import de.uniba.dsg.jpb.server.model.Warehouse;
import de.uniba.dsg.jpb.util.UniformRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FakeDataGenerator {

  private static final String BAD_CREDIT = "BC";
  private static final String GOOD_CREDIT = "GC";
  private static final String ORIGINAL = "ORIGINAL";
  private final int warehouseCount;
  private final int itemCount;
  private final int districtsPerWarehouseCount;
  private final int customersPerDistrictCount;
  private final int ordersPerDistrictCount;
  private final Faker faker;
  private final UniformRandom salesTaxRandom;
  private final List<NewOrder> newOrders;
  private List<Warehouse> warehouses;
  private List<Item> items;

  public FakeDataGenerator(int warehouseCount, boolean limited) {
    faker = new Faker(Locale.US);
    salesTaxRandom = new UniformRandom(0.0, 0.2, 1);
    this.warehouseCount = warehouseCount;
    if (limited) {
      itemCount = 1_000;
      districtsPerWarehouseCount = 10;
      customersPerDistrictCount = 30;
      ordersPerDistrictCount = 30;
    } else {
      itemCount = 100_000;
      districtsPerWarehouseCount = 10;
      customersPerDistrictCount = 3_000;
      ordersPerDistrictCount = 3_000;
    }
    newOrders = new ArrayList<>();
    warehouses = null;
    items = null;
  }

  public List<Warehouse> getWarehouses() {
    return warehouses;
  }

  public List<Item> getItems() {
    return items;
  }

  public List<NewOrder> getNewOrders() {
    return newOrders;
  }

  public void generate() {
    newOrders.clear();
    items = generateItems();
    warehouses = generateWarehouses();
  }

  private List<Warehouse> generateWarehouses() {
    List<Warehouse> warehouses = new ArrayList<>(warehouseCount);
    List<Address> addresses = generateAddresses(warehouseCount);
    for (int i = 0; i < warehouseCount; i++) {
      Warehouse warehouse = new Warehouse();
      warehouse.setName(faker.address().cityName());
      warehouse.setAddress(addresses.get(i));
      warehouse.setSalesTax(salesTaxRandom.nextDouble());
      warehouse.setYearToDateBalance(300_000);
      warehouse.setDistricts(generateDistricts(warehouse));
      warehouse.setStocks(generateStocks(warehouse, items));
      warehouses.add(warehouse);
    }
    return warehouses;
  }

  private List<Item> generateItems() {
    UniformRandom priceRandom = new UniformRandom(1.0, 100.0, 2);
    UniformRandom imageIdRandom = new UniformRandom(1_000_000, 5_000_000);
    List<Item> items = new ArrayList<>(itemCount);
    for (int i = 0; i < itemCount; i++) {
      Item item = new Item();
      item.setImageId(imageIdRandom.nextLong());
      item.setName(faker.commerce().productName());
      if (i % 10_000 == 0) {
        item.setData(insertOriginal(lorem26To50()));
      } else {
        item.setData(lorem26To50());
      }
      item.setPrice(priceRandom.nextDouble());
      items.add(item);
    }
    return items;
  }

  private List<Stock> generateStocks(Warehouse warehouse, List<Item> items) {
    List<Stock> stocks = new ArrayList<>(items.size());
    final int length = 24;
    UniformRandom quantityRandom = new UniformRandom(10, 100);
    for (Item item : items) {
      Stock stock = new Stock();
      stock.setItem(item);
      stock.setWarehouse(warehouse);
      stock.setDist01(loremFixedLength(length));
      stock.setDist02(loremFixedLength(length));
      stock.setDist03(loremFixedLength(length));
      stock.setDist04(loremFixedLength(length));
      stock.setDist05(loremFixedLength(length));
      stock.setDist06(loremFixedLength(length));
      stock.setDist07(loremFixedLength(length));
      stock.setDist08(loremFixedLength(length));
      stock.setDist09(loremFixedLength(length));
      stock.setDist10(loremFixedLength(length));
      stock.setData(lorem26To50());
      stock.setOrderCount(0);
      stock.setRemoteCount(0);
      stock.setQuantity(quantityRandom.nextInt());
      stocks.add(stock);
    }
    return stocks;
  }

  private List<Address> generateAddresses(int count) {
    List<Address> addresses = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      Address address = new Address();
      address.setStreet1(faker.address().streetAddress());
      address.setStreet2(faker.address().secondaryAddress());
      address.setCity(faker.address().cityName());
      address.setState(faker.address().stateAbbr());
      address.setZipCode(faker.address().zipCodeByState(address.getState()));
      addresses.add(address);
    }
    return addresses;
  }

  private List<Order> generateOrders(District district, List<Item> items) {
    if (district.getCustomers() == null
        || district.getCustomers().size() != customersPerDistrictCount
        || items.size() != itemCount) {
      throw new IllegalArgumentException();
    }
    List<Order> orders = new ArrayList<>(ordersPerDistrictCount);
    List<Customer> shuffledCustomers = new ArrayList<>(district.getCustomers());
    Collections.shuffle(shuffledCustomers);
    UniformRandom carrierIdRandom = new UniformRandom(1, 10);
    UniformRandom orderLineCountRandom = new UniformRandom(5, 15);
    UniformRandom random = new UniformRandom(1, 9);
    for (int i = 0; i < ordersPerDistrictCount; i++) {
      Customer customer = shuffledCustomers.get(i);
      Order order = new Order();
      order.setCustomer(customer);
      order.setDistrict(customer.getDistrict());
      order.setEntryDate(LocalDateTime.now());
      order.setCarrierId(random.nextInt() < 7 ? carrierIdRandom.nextLong() : null);
      order.setOrderLineCount(orderLineCountRandom.nextInt());
      order.setAllLocal(true);
      order.setOrderLines(generateOrderLines(order, items));
      orders.add(order);
    }
    return orders;
  }

  private List<OrderLine> generateOrderLines(Order order, List<Item> items) {
    if (items.size() != itemCount) {
      throw new IllegalArgumentException();
    }
    List<OrderLine> lines = new ArrayList<>(order.getOrderLineCount());
    UniformRandom itemIdxRandom = new UniformRandom(0, items.size() - 1);
    UniformRandom amountRandom = new UniformRandom(0.01, 9_999.9, 2);
    UniformRandom random = new UniformRandom(1, 9);
    for (int i = 0; i < order.getOrderLineCount(); i++) {
      OrderLine line = new OrderLine();
      line.setOrder(order);
      line.setNumber(i + 1);
      line.setItem(items.get(itemIdxRandom.nextInt()));
      line.setSupplyingWarehouse(order.getDistrict().getWarehouse());
      line.setDeliveryDate(random.nextInt() < 7 ? order.getEntryDate() : null);
      line.setQuantity(5);
      line.setAmount(random.nextInt() < 7 ? 0.0 : amountRandom.nextDouble());
      line.setDistInfo(loremFixedLength(24));
      lines.add(line);
    }
    return lines;
  }

  private List<District> generateDistricts(Warehouse warehouse) {
    List<District> districts = new ArrayList<>(10);
    List<Address> addresses = generateAddresses(districtsPerWarehouseCount);
    for (int i = 0; i < districtsPerWarehouseCount; i++) {
      District district = new District();
      district.setWarehouse(warehouse);
      districts.add(district);
      district.setName(faker.address().cityName());
      district.setAddress(addresses.get(i));
      district.setSalesTax(salesTaxRandom.nextDouble());
      district.setYearToDateBalance(30_000);
      district.setCustomers(generateCustomers(district));
      district.setOrders(generateOrders(district, items));
      newOrders.addAll(generateNewOrders(district));
    }
    return districts;
  }

  private List<NewOrder> generateNewOrders(District district) {
    List<NewOrder> newOrders = new ArrayList<>();
    List<Order> orders;
    int orderCount = district.getOrders().size();
    if (orderCount == 3_000) {
      orders = district.getOrders().subList(2100, 3000);
    } else {
      orders = district.getOrders().subList((int) (orderCount * (2f / 3)), orderCount);
    }
    for (Order order : orders) {
      NewOrder newOrder = new NewOrder();
      newOrder.setOrder(order);
      newOrders.add(newOrder);
    }
    return newOrders;
  }

  private List<Customer> generateCustomers(District district) {
    List<Customer> customers = new ArrayList<>(customersPerDistrictCount);
    List<Address> addresses = generateAddresses(customersPerDistrictCount);
    UniformRandom discountRandom = new UniformRandom(0.0, 0.5, 2);
    UniformRandom creditRandom = new UniformRandom(1, 100);
    for (int i = 0; i < customersPerDistrictCount; i++) {
      Customer customer = new Customer();
      customer.setDistrict(district);
      customer.setAddress(addresses.get(i));
      customer.setFirstName(faker.name().firstName());
      customer.setMiddleName(faker.name().firstName());
      customer.setLastName(faker.name().lastName());
      customer.setPhoneNumber(faker.phoneNumber().phoneNumber());
      customer.setSince(LocalDateTime.now());
      customer.setHistory(generateHistory(customer));
      customer.setCredit(creditRandom.nextInt() < 11 ? BAD_CREDIT : GOOD_CREDIT);
      customer.setCreditLimit(50_000);
      customer.setDiscount(discountRandom.nextDouble());
      customer.setBalance(-10.0);
      customer.setYearToDatePayment(10.0);
      customer.setPaymentCount(1);
      customer.setDeliveryCount(0);
      customer.setData(lorem(300, 500));
      customers.add(customer);
    }
    return customers;
  }

  private History generateHistory(Customer customer) {
    History history = new History();
    history.setCustomer(customer);
    history.setDistrict(customer.getDistrict());
    history.setDate(LocalDateTime.now());
    history.setAmount(10.0);
    history.setData(lorem26To50());
    return history;
  }

  private String insertOriginal(String s) {
    if (s.length() < 26 || s.length() > 50) {
      throw new IllegalArgumentException();
    }
    UniformRandom indexRandom = new UniformRandom(0, 17);
    int index = indexRandom.nextInt();
    return s.substring(0, index) + ORIGINAL + s.substring(index + ORIGINAL.length());
  }

  private String loremFixedLength(int length) {
    return faker.lorem().characters(length);
  }

  private String lorem(int minimumLength, int maximumLength) {
    return faker.lorem().characters(minimumLength, maximumLength, true, true);
  }

  private String lorem26To50() {
    return lorem(26, 50);
  }
}
