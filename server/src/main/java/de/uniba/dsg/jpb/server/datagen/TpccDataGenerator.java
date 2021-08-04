package de.uniba.dsg.jpb.server.datagen;

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
import de.uniba.dsg.jpb.util.RandomSelector;
import de.uniba.dsg.jpb.util.StringRandom;
import de.uniba.dsg.jpb.util.UniformRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TpccDataGenerator {

  private static final List<String> LAST_NAME_SYLLABLES =
      List.of("BAR", "OUGHT", "ABLE", "PRI", "PRES", "ESE", "ANTI", "CALLY", "ATION", "EING");
  private final StringRandom dataRandom;
  private final StringRandom nameRandom;
  private final UniformRandom salesTaxRandom;
  private final int warehouseCount;
  private final int itemCount;
  private final int districtsPerWarehouseCount;
  private final int customersPerDistrictCount;
  private final int ordersPerDistrictCount;
  private final List<String> zipCodes;
  private List<Item> items;
  private List<Warehouse> warehouses;
  private final List<NewOrder> newOrders;
  private final boolean limited;

  public TpccDataGenerator(int warehouseCount) {
    dataRandom = new StringRandom(26, 50);
    nameRandom = new StringRandom(6, 10);
    salesTaxRandom = new UniformRandom(0.0, 0.2);
    this.warehouseCount = warehouseCount;
    itemCount = 100_000;
    districtsPerWarehouseCount = 10;
    customersPerDistrictCount = 3_000;
    ordersPerDistrictCount = 3_000;
    zipCodes = generateZipCodes();
    items = null;
    warehouses = null;
    newOrders = new ArrayList<>();
    limited = false;
  }

  public TpccDataGenerator(int warehouseCount, boolean limited) {
    dataRandom = new StringRandom(26, 50);
    nameRandom = new StringRandom(6, 10);
    salesTaxRandom = new UniformRandom(0.0, 0.2);
    this.warehouseCount = warehouseCount;
    itemCount = 1_000;
    districtsPerWarehouseCount = 10;
    customersPerDistrictCount = 30;
    ordersPerDistrictCount = 30;
    zipCodes = generateZipCodes();
    items = null;
    warehouses = null;
    newOrders = new ArrayList<>();
    this.limited = limited;
  }

  public void generate() {
    newOrders.clear();
    items = generateItems();
    warehouses = generateWarehouses();
    if (limited) {
      // Don't verify in the limited mode, because this will most certainly fail
      return;
    }
    // Verify based on spec 4.3.3.1
    for (Warehouse warehouse : warehouses) {
      if (warehouse.getStocks().size() != itemCount) {
        throw new IllegalStateException("Unexpected Stock count: " + warehouse.getStocks().size());
      }
      if (warehouse.getDistricts().size() != districtsPerWarehouseCount) {
        throw new IllegalStateException(
            "Unexpected District count: " + warehouse.getDistricts().size());
      }
      for (District district : warehouse.getDistricts()) {
        if (district.getCustomers().size() != customersPerDistrictCount) {
          throw new IllegalStateException(
              "Unexpected Customer count: " + district.getCustomers().size());
        }
        for (Customer customer : district.getCustomers()) {
          if (customer.getHistory() == null) {
            throw new IllegalStateException("Missing History");
          }
        }
        if (district.getOrders().size() != ordersPerDistrictCount) {
          throw new IllegalStateException("Unexpected Order count: " + district.getOrders().size());
        }
        for (Order order : district.getOrders()) {
          if (order.getOrderLineCount() != order.getOrderLines().size()) {
            throw new IllegalStateException(
                "Unexpected OrderLine count: " + order.getOrderLines().size());
          }
        }
      }
    }
    if (newOrders.size() != warehouseCount * districtsPerWarehouseCount * 900) {
      throw new IllegalStateException("Unexpected NewOrder count: " + newOrders.size());
    }
  }

  public void generateTestData() {
    newOrders.clear();
    items = generateItems();
    warehouses = generateWarehouses();
  }

  public List<Item> getItems() {
    return items;
  }

  public List<Warehouse> getWarehouses() {
    return warehouses;
  }

  public List<NewOrder> getNewOrders() {
    return newOrders;
  }

  private List<Warehouse> generateWarehouses() {
    List<Warehouse> warehouses = new ArrayList<>(warehouseCount);
    List<Address> addresses = generateAddresses(warehouseCount);
    for (int i = 0; i < warehouseCount; i++) {
      Warehouse warehouse = new Warehouse();
      warehouse.setId((long) i + 1);
      warehouse.setName(nameRandom.nextString());
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
    UniformRandom imageIdRandom = new UniformRandom(1, 10_000);
    StringRandom nameRandom = new StringRandom(14, 24);
    UniformRandom priceRandom = new UniformRandom(1.0, 100.0);
    List<Item> items = new ArrayList<>(itemCount);
    for (int i = 0; i < itemCount; i++) {
      Item item = new Item();
      item.setId((long) i + 1);
      item.setName(nameRandom.nextString());
      item.setImageId(imageIdRandom.nextLong());
      item.setPrice(priceRandom.nextDouble());
      if (i % 10_000 == 0) {
        item.setData(insertOriginal(dataRandom.nextString()));
      } else {
        item.setData(dataRandom.nextString());
      }
      items.add(item);
    }
    return items;
  }

  private List<Stock> generateStocks(Warehouse warehouse, List<Item> items) {
    // Per warehouse
    List<Stock> stocks = new ArrayList<>(items.size());
    StringRandom distRandom = new StringRandom(24);
    UniformRandom quantityRandom = new UniformRandom(10, 100);
    for (int i = 0; i < items.size(); i++) {
      Stock stock = new Stock();
      stock.setItem(items.get(i));
      stock.setWarehouse(warehouse);
      stock.setDist01(distRandom.nextString());
      stock.setDist02(distRandom.nextString());
      stock.setDist03(distRandom.nextString());
      stock.setDist04(distRandom.nextString());
      stock.setDist05(distRandom.nextString());
      stock.setDist06(distRandom.nextString());
      stock.setDist07(distRandom.nextString());
      stock.setDist08(distRandom.nextString());
      stock.setDist09(distRandom.nextString());
      stock.setDist10(distRandom.nextString());
      stock.setData(dataRandom.nextString());
      stock.setOrderCount(0);
      stock.setRemoteCount(0);
      stock.setQuantity(quantityRandom.nextInt());
      stocks.add(stock);
    }
    return stocks;
  }

  private List<District> generateDistricts(Warehouse warehouse) {
    List<District> districts = new ArrayList<>(10);
    List<Address> addresses = generateAddresses(districtsPerWarehouseCount);
    for (int i = 0; i < districtsPerWarehouseCount; i++) {
      District district = new District();
      district.setId((long) i + 1);
      district.setWarehouse(warehouse);
      districts.add(district);
      district.setName(nameRandom.nextString());
      district.setAddress(addresses.get(i));
      district.setSalesTax(salesTaxRandom.nextDouble());
      district.setYearToDateBalance(30_000);
      district.setCustomers(generateCustomers(district));
      district.setOrders(generateOrders(district, items));
      newOrders.addAll(generateNewOrders(district));
    }
    return districts;
  }

  private List<Customer> generateCustomers(District district) {
    List<Customer> customers = new ArrayList<>(customersPerDistrictCount);
    List<Address> addresses = generateAddresses(customersPerDistrictCount);
    List<String> lastNames = generateLastNames(customersPerDistrictCount);
    StringRandom firstNameRandom = new StringRandom(8, 16);
    StringRandom phoneNumberRandom =
        new StringRandom(16, new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'});
    UniformRandom discountRandom = new UniformRandom(0.0, 0.5);
    UniformRandom creditRandom = new UniformRandom(0, 100);
    StringRandom dataRandom = new StringRandom(300, 500);
    for (int i = 0; i < customersPerDistrictCount; i++) {
      Customer customer = new Customer();
      customer.setId((long) i + 1);
      customer.setDistrict(district);
      customer.setAddress(addresses.get(i));
      customer.setFirstName(firstNameRandom.nextString());
      customer.setMiddleName("OE");
      // FIXME this is deviating from spec (p 67)
      customer.setLastName(lastNames.get(i));
      customer.setPhoneNumber(phoneNumberRandom.nextString());
      customer.setSince(LocalDateTime.now());
      customer.setHistory(generateHistory(customer));
      customer.setCredit(creditRandom.nextInt() > 10 ? "BC" : "GC");
      customer.setCreditLimit(50_000);
      customer.setDiscount(discountRandom.nextDouble());
      customer.setBalance(-10.0);
      customer.setYearToDatePayment(10.0);
      customer.setPaymentCount(1);
      customer.setDeliveryCount(0);
      customer.setData(dataRandom.nextString());
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
    history.setData(dataRandom.nextString());
    return history;
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
    for (int i = 0; i < ordersPerDistrictCount; i++) {
      Customer customer = shuffledCustomers.get(i);
      Order order = new Order();
      order.setId((long) i + 1);
      order.setCustomer(customer);
      order.setDistrict(customer.getDistrict());
      order.setEntryDate(LocalDateTime.now());
      order.setCarrierId(order.getId() < 2_101 ? carrierIdRandom.nextLong() : null);
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
    UniformRandom amountRandom = new UniformRandom(0.01, 9_999.9);
    StringRandom distRandom = new StringRandom(24);
    for (int i = 0; i < order.getOrderLineCount(); i++) {
      OrderLine line = new OrderLine();
      line.setOrder(order);
      line.setNumber(i + 1);
      line.setItem(items.get(itemIdxRandom.nextInt()));
      line.setSupplyingWarehouse(order.getDistrict().getWarehouse());
      line.setDeliveryDate(order.getId() < 2_101 ? order.getEntryDate() : null);
      line.setQuantity(5);
      line.setAmount(order.getId() < 2_101 ? 0.0 : amountRandom.nextDouble());
      line.setDistInfo(distRandom.nextString());
      lines.add(line);
    }
    return lines;
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

  private List<Address> generateAddresses(int count) {
    StringRandom stateRandom = new StringRandom(2);
    StringRandom random = new StringRandom(10, 20);
    RandomSelector<String> zipSelector = new RandomSelector<>(zipCodes);
    List<Address> addresses = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      Address address = new Address();
      address.setStreet1(random.nextString());
      address.setStreet2(random.nextString());
      address.setCity(random.nextString());
      address.setState(stateRandom.nextString());
      address.setZipCode(zipSelector.next());
      addresses.add(address);
    }
    return addresses;
  }

  private List<String> generateLastNames(int count) {
    List<String> names = new ArrayList<>(count);
    UniformRandom random = new UniformRandom(0, 999);
    for (int i = 0; i < count; i++) {
      int n = random.nextInt();
      List<Integer> digits = toDigits(n);
      String name;
      if (n > 99) {
        name = LAST_NAME_SYLLABLES.get(digits.get(0));
        name += LAST_NAME_SYLLABLES.get(digits.get(1));
        name += LAST_NAME_SYLLABLES.get(digits.get(2));
      } else if (n > 9) {
        name = LAST_NAME_SYLLABLES.get(0);
        name += LAST_NAME_SYLLABLES.get(digits.get(0));
        name += LAST_NAME_SYLLABLES.get(digits.get(1));
      } else {
        name = LAST_NAME_SYLLABLES.get(0);
        name += name;
        name += LAST_NAME_SYLLABLES.get(digits.get(0));
      }
      names.add(name);
    }
    return names;
  }

  private static List<Integer> toDigits(int number) {
    if (number == 0) {
      return List.of(0);
    }
    List<Integer> digits = new ArrayList<>();
    while (number > 0) {
      digits.add(number % 10);
      number = number / 10;
    }
    Collections.reverse(digits);
    return digits;
  }

  private String insertOriginal(String s) {
    final String original = "ORIGINAL";
    if (s.length() < 26 || s.length() > 50) {
      throw new IllegalArgumentException();
    }
    UniformRandom indexRandom = new UniformRandom(0, 17);
    int index = indexRandom.nextInt();
    return s.substring(0, index) + original + s.substring(index + original.length());
  }

  private List<String> generateZipCodes() {
    List<String> codes = new ArrayList<>(10_000);
    List<String> prefixes = fourDigitsList();
    for (String prefix : prefixes) {
      codes.add(prefix + "11111");
    }
    return codes;
  }

  private List<String> fourDigitsList() {
    List<String> strings = new ArrayList<>(10_000);
    for (int i = 0; i < 10_000; i++) {
      String s = "";
      if (i < 10) {
        s += "000" + i;
      } else if (i < 100) {
        s += "00" + i;
      } else if (i < 1_000) {
        s += "0" + i;
      } else {
        s += i;
      }
      strings.add(s);
    }
    return strings;
  }

  private String numberString(int length) {
    UniformRandom random = new UniformRandom(0, 9);
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < length; i++) {
      s.append(random.nextInt());
    }
    return s.toString();
  }
}
