package de.uniba.dsg.wss.data.gen;

import static java.util.Objects.requireNonNull;

import com.github.javafaker.Faker;
import de.uniba.dsg.wss.commons.RandomSelector;
import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.commons.UniformRandom;
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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DataGenerator {

  private static final Logger LOG = LogManager.getLogger(DataGenerator.class);
  private static final String BAD_CREDIT = "BC";
  private static final String GOOD_CREDIT = "GC";
  private static final String ORIGINAL = "ORIGINAL";
  private static final String DEFAULT_PASSWORD = "password";
  private static final String EMPLOYEE_USERNAME_PREFIX = "terminal_user_";
  private static final List<String> EMAIL_SERVICES =
      List.of(
          "outlook.com",
          "gmail.com",
          "icloud.com",
          "mail.com",
          "yahoo.com",
          "gmx.com",
          "hotmail.com",
          "protonmail.com");
  private static final List<String> CARRIER_NAMES =
      List.of(
          "Warehouse Services Inc.",
          "Dupré Logistics",
          "Hub Group",
          "Averitt Express",
          "UPS Supply Chain Solutions",
          "DHL Supply Chain",
          "Commercial Warehousing",
          "Great Plains Transport",
          "PAM Transportation",
          "FedEx");
  private final int warehouseCount;
  private final int productCount;
  private final int districtsPerWarehouseCount;
  private final int customersPerDistrictCount;
  private final int ordersPerDistrictCount;
  private final Faker faker;
  private final UniformRandom salesTaxRandom;
  private final UniformRandom oneInThreeRandom;
  private final RandomSelector<String> emailService;
  private List<Product> products;
  private List<Carrier> carriers;
  private List<Warehouse> warehouses;
  private final List<Employee> employees;
  private final List<String> existingEmails;
  private final PasswordEncoder passwordEncoder;
  private final LocalDateTime now;

  /**
   * Constructs a new data generator. This constructor is primarily meant for debugging and testing
   * purposes. It is very much possible to provide parameters to it that will result in invalid data
   * being generated.
   *
   * @param warehouses the number of warehouses to generate
   * @param districts the number of districts per warehouse to generate
   * @param customers the number of customers per district
   * @param orders the number of orders per district
   * @param products the number of products
   * @param passwordEncoder the encoder to be used to encode the employee credentials
   */
  public DataGenerator(
      int warehouses,
      int districts,
      int customers,
      int orders,
      int products,
      PasswordEncoder passwordEncoder) {
    if (warehouses < 1 || districts < 1 || customers < 1 || orders < 1 || products < 1) {
      throw new IllegalArgumentException("Warehouse count must be greater than zero");
    }
    warehouseCount = warehouses;
    productCount = products;
    districtsPerWarehouseCount = districts;
    customersPerDistrictCount = customers;
    ordersPerDistrictCount = orders;
    faker = new Faker(Locale.US);
    salesTaxRandom = new UniformRandom(0.0, 0.2, 1);
    oneInThreeRandom = new UniformRandom(1, 3);
    emailService = new RandomSelector<>(EMAIL_SERVICES);
    this.products = null;
    carriers = null;
    this.warehouses = null;
    employees = new ArrayList<>();
    existingEmails = new ArrayList<>();
    this.passwordEncoder = requireNonNull(passwordEncoder);
    now = LocalDateTime.now();
  }

  public DataGenerator(int warehouseCount, boolean fullScale, PasswordEncoder passwordEncoder) {
    if (warehouseCount < 1) {
      throw new IllegalArgumentException("Warehouse count must be greater than zero");
    }
    this.warehouseCount = warehouseCount;
    if (fullScale) {
      productCount = 100_000;
      districtsPerWarehouseCount = 10;
      customersPerDistrictCount = 3_000;
      ordersPerDistrictCount = 3_000;
    } else {
      productCount = 1_000;
      districtsPerWarehouseCount = 10;
      customersPerDistrictCount = 30;
      ordersPerDistrictCount = 30;
    }
    faker = new Faker(Locale.US);
    salesTaxRandom = new UniformRandom(0.0, 0.2, 1);
    oneInThreeRandom = new UniformRandom(1, 3);
    emailService = new RandomSelector<>(EMAIL_SERVICES);
    products = null;
    carriers = null;
    warehouses = null;
    employees = new ArrayList<>();
    existingEmails = new ArrayList<>();
    this.passwordEncoder = requireNonNull(passwordEncoder);
    now = LocalDateTime.now();
  }

  public DataGenerator(int warehouseCount, PasswordEncoder passwordEncoder) {
    this(warehouseCount, true, passwordEncoder);
  }

  public List<Warehouse> getWarehouses() {
    return warehouses;
  }

  public List<Employee> getEmployees() {
    return employees;
  }

  public List<Product> getProducts() {
    return products;
  }

  public List<Carrier> getCarriers() {
    return carriers;
  }

  public boolean isDataGenerated() {
    return warehouses != null;
  }

  public void generate() {
    Stopwatch stopwatch = new Stopwatch(true);
    LOG.info(
        "Generating {} products, {} warehouses, {} districts, {} employees, {} customers, and {} orders",
        productCount,
        warehouseCount,
        warehouseCount * districtsPerWarehouseCount,
        warehouseCount * districtsPerWarehouseCount,
        warehouseCount * districtsPerWarehouseCount * customersPerDistrictCount,
        warehouseCount * districtsPerWarehouseCount * ordersPerDistrictCount);
    employees.clear();
    existingEmails.clear();
    products = generateProducts();
    carriers = generateCarriers();
    warehouses = generateWarehouses();
    stopwatch.stop();
    LOG.info("Generated {} model data entities, took {}", countEntities(), stopwatch.getDuration());
  }

  private int countEntities() {
    int entityCount = employees.size();
    entityCount += products.size();
    entityCount += carriers.size();
    entityCount +=
        warehouses.stream()
            .mapToInt(
                w -> {
                  int warehouseEntities = w.getStocks().size();
                  warehouseEntities +=
                      w.getDistricts().stream()
                          .mapToInt(
                              d -> {
                                int districtEntities = d.getCustomers().size();
                                districtEntities +=
                                    d.getCustomers().stream()
                                        .mapToInt(c -> c.getPayments().size())
                                        .sum();
                                districtEntities +=
                                    d.getOrders().stream()
                                        .mapToInt(o -> o.getItems().size() + 1)
                                        .sum();
                                return districtEntities + 1;
                              })
                          .sum();
                  return warehouseEntities + 1;
                })
            .sum();
    return entityCount;
  }

  private List<Product> generateProducts() {
    UniformRandom priceRandom = new UniformRandom(1.0, 100.0, 2);
    List<Product> products = new ArrayList<>(productCount);
    for (int i = 0; i < productCount; i++) {
      Product product = new Product();
      product.setImagePath(faker.file().fileName(null, null, "jpg", "/"));
      product.setName(faker.commerce().productName());
      if (i % 10_000 == 0) {
        product.setData(insertOriginal(lorem26To50()));
      } else {
        product.setData(lorem26To50());
      }
      product.setPrice(priceRandom.nextDouble());
      products.add(product);
    }
    LOG.debug("Generated {} products", products.size());
    return products;
  }

  private List<Carrier> generateCarriers() {
    List<Carrier> carriers = new ArrayList<>(CARRIER_NAMES.size());
    List<Address> addresses = generateAddresses(CARRIER_NAMES.size());
    for (int i = 0; i < CARRIER_NAMES.size(); i++) {
      Carrier carrier = new Carrier();
      carrier.setName(CARRIER_NAMES.get(i));
      carrier.setPhoneNumber(faker.phoneNumber().phoneNumber());
      carrier.setAddress(addresses.get(i));
      carriers.add(carrier);
    }
    LOG.debug("Generated {} carriers", carriers.size());
    return carriers;
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
      warehouse.setDistricts(generateDistricts(warehouse, i + 1));
      warehouse.setStocks(generateStocks(warehouse, products));
      warehouses.add(warehouse);
    }
    LOG.debug("Generated {} warehouses", warehouses.size());
    return warehouses;
  }

  private List<District> generateDistricts(Warehouse warehouse, int warehouseNbr) {
    List<District> districts = new ArrayList<>(10);
    List<Address> addresses =
        generateAddresses(districtsPerWarehouseCount, warehouse.getAddress().getState());
    for (int i = 0; i < districtsPerWarehouseCount; i++) {
      District district = new District();
      district.setWarehouse(warehouse);
      districts.add(district);
      district.setName(faker.address().cityName());
      district.setAddress(addresses.get(i));
      district.setSalesTax(salesTaxRandom.nextDouble());
      district.setYearToDateBalance(30_000);
      district.setCustomers(generateCustomers(district));
      district.setOrders(generateOrders(district, products));
      employees.add(generateEmployee(district, warehouseNbr, i + 1));
    }
    LOG.debug("Generated {} districts", districts.size());
    return districts;
  }

  private Employee generateEmployee(District district, int warehouseNbr, int districtNbr) {
    Employee employee = new Employee();
    employee.setFirstName(faker.name().firstName());
    employee.setMiddleName(faker.name().firstName());
    employee.setLastName(faker.name().lastName());
    employee.setPhoneNumber(faker.phoneNumber().phoneNumber());
    employee.setEmail(
        generateUniqueEmail(
            employee.getFirstName(), employee.getMiddleName(), employee.getLastName()));
    employee.setAddress(newAddressSameZip(district.getAddress()));
    String postfix = warehouseNbr + "_" + districtNbr;
    employee.setUsername(EMPLOYEE_USERNAME_PREFIX + postfix);
    employee.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD + "_" + postfix));
    employee.setDistrict(district);
    employee.setTitle(faker.job().title());
    return employee;
  }

  private Address newAddressSameZip(Address address) {
    Address a = new Address(address);
    a.setStreet1(faker.address().streetAddress());
    a.setStreet2(faker.address().secondaryAddress());
    return a;
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
      customer.setEmail(
          generateUniqueEmail(
              customer.getFirstName(), customer.getMiddleName(), customer.getLastName()));
      customer.setSince(randomTimeBefore(now.minusMonths(2), 3));
      List<Payment> payments = new ArrayList<>();
      payments.add(generatePayment(customer));
      customer.setPayments(payments);
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
    LOG.debug("Generated {} customers", customers.size());
    return customers;
  }

  private Payment generatePayment(Customer customer) {
    Payment payment = new Payment();
    payment.setCustomer(customer);
    payment.setDistrict(customer.getDistrict());
    payment.setDate(randomTimeAfter(customer.getSince()));
    payment.setAmount(10.0);
    payment.setData(lorem26To50());
    return payment;
  }

  private List<Stock> generateStocks(Warehouse warehouse, List<Product> products) {
    List<Stock> stocks = new ArrayList<>(products.size());
    final int length = 24;
    UniformRandom quantityRandom = new UniformRandom(10, 100);
    for (Product product : products) {
      Stock stock = new Stock();
      stock.setProduct(product);
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
    LOG.debug("Generated {} stocks", stocks.size());
    return stocks;
  }

  private List<Address> generateAddresses(int count) {
    List<Address> addresses = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      Address address = new Address();
      address.setStreet1(faker.address().streetAddress());
      address.setStreet2(faker.address().secondaryAddress());
      address.setState(faker.address().stateAbbr());
      address.setZipCode(faker.address().zipCodeByState(address.getState()));
      address.setCity(faker.address().cityName());
      addresses.add(address);
    }
    return addresses;
  }

  private List<Address> generateAddresses(int count, String state) {
    List<Address> addresses = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      Address address = new Address();
      address.setStreet1(faker.address().streetAddress());
      address.setStreet2(faker.address().secondaryAddress());
      address.setState(state);
      address.setZipCode(faker.address().zipCodeByState(address.getState()));
      address.setCity(faker.address().cityName());
      addresses.add(address);
    }
    return addresses;
  }

  private List<Order> generateOrders(District district, List<Product> products) {
    if (district.getCustomers() == null
        || district.getCustomers().size() != customersPerDistrictCount
        || products.size() != productCount) {
      throw new IllegalArgumentException();
    }
    List<Order> orders = new ArrayList<>(ordersPerDistrictCount);
    List<Customer> shuffledCustomers = new ArrayList<>(district.getCustomers());
    Collections.shuffle(shuffledCustomers);
    UniformRandom carrierIdRandom = new UniformRandom(1, 10);
    RandomSelector<Carrier> carrierSelector = new RandomSelector<>(carriers);
    UniformRandom orderItemCountRandom = new UniformRandom(5, 15);
    for (int i = 0; i < ordersPerDistrictCount; i++) {
      Customer customer = shuffledCustomers.get(i);
      Order order = new Order();
      order.setCustomer(customer);
      order.setDistrict(customer.getDistrict());
      order.setEntryDate(randomTimeAfter(customer.getSince()));
      order.setCarrier(oneInThreeRandom.nextInt() < 3 ? carrierSelector.next() : null);
      order.setItemCount(orderItemCountRandom.nextInt());
      order.setAllLocal(true);
      order.setItems(generateOrderItems(order, products));
      orders.add(order);
    }
    LOG.debug("Generated {} orders", orders.size());
    return orders;
  }

  private List<OrderItem> generateOrderItems(Order order, List<Product> products) {
    if (products.size() != productCount) {
      throw new IllegalArgumentException();
    }
    List<OrderItem> orderItems = new ArrayList<>(order.getItemCount());
    UniformRandom itemIdxRandom = new UniformRandom(0, products.size() - 1);
    UniformRandom amountRandom = new UniformRandom(0.01, 9_999.9, 2);
    for (int i = 0; i < order.getItemCount(); i++) {
      OrderItem orderItem = new OrderItem();
      orderItem.setOrder(order);
      orderItem.setNumber(i + 1);
      orderItem.setProduct(products.get(itemIdxRandom.nextInt()));
      orderItem.setSupplyingWarehouse(order.getDistrict().getWarehouse());
      orderItem.setDeliveryDate(oneInThreeRandom.nextInt() < 3 ? order.getEntryDate() : null);
      orderItem.setQuantity(5);
      orderItem.setAmount(oneInThreeRandom.nextInt() < 3 ? 0.0 : amountRandom.nextDouble());
      orderItem.setDistInfo(loremFixedLength(24));
      orderItems.add(orderItem);
    }
    return orderItems;
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

  private String generateUniqueEmail(String firstName, String middleName, String lastName) {
    final String lcFirst = firstName.toLowerCase(Locale.ROOT);
    final String lcMiddle = middleName.toLowerCase(Locale.ROOT);
    final String lcLast = lastName.toLowerCase(Locale.ROOT);
    final String domain = emailService.next();
    String email = lcFirst + "-" + lcMiddle + "." + lcLast + "@" + domain;
    int i = 1;
    while (existingEmails.contains(email)) {
      email = lcFirst + "-" + lcMiddle + "." + lcLast + i + "@" + domain;
      i++;
    }
    existingEmails.add(email);
    return email;
  }

  private static LocalDateTime randomTimeBefore(LocalDateTime before, int maxMonthOffset) {
    if (maxMonthOffset < 1 || maxMonthOffset > 12) {
      throw new IllegalArgumentException();
    }
    int minYear;
    int minMonth;
    int minDay = 15;
    if (before.getMonthValue() <= maxMonthOffset) {
      minYear = before.getYear() - 1;
      minMonth = 12 - maxMonthOffset + before.getMonthValue();
    } else {
      minYear = before.getYear();
      minMonth = before.getMonthValue() - maxMonthOffset;
    }
    long minSeconds =
        LocalDateTime.of(
                minYear, minMonth, minDay, (int) (Math.random() * 23), (int) (Math.random() * 59))
            .toEpochSecond(ZoneOffset.UTC);
    long maxSeconds = before.toEpochSecond(ZoneOffset.UTC);
    long randomDay = ThreadLocalRandom.current().nextLong(minSeconds, maxSeconds);
    return LocalDateTime.ofEpochSecond(
        randomDay, (int) (Math.random() * before.getNano()), ZoneOffset.UTC);
  }

  private static LocalDateTime randomTimeAfter(LocalDateTime min) {
    long minSeconds = min.toEpochSecond(ZoneOffset.UTC);
    long maxSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    long randomDay = ThreadLocalRandom.current().nextLong(minSeconds, maxSeconds);
    return LocalDateTime.ofEpochSecond(
        randomDay, (int) (Math.random() * min.getNano()), ZoneOffset.UTC);
  }
}