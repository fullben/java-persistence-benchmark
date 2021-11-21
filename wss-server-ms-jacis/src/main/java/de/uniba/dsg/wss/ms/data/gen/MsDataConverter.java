package de.uniba.dsg.wss.ms.data.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.gen.DataConverter;
import de.uniba.dsg.wss.data.gen.DataGenerator;
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
import de.uniba.dsg.wss.ms.data.model.AddressData;
import de.uniba.dsg.wss.ms.data.model.CarrierData;
import de.uniba.dsg.wss.ms.data.model.CustomerData;
import de.uniba.dsg.wss.ms.data.model.DistrictData;
import de.uniba.dsg.wss.ms.data.model.EmployeeData;
import de.uniba.dsg.wss.ms.data.model.OrderData;
import de.uniba.dsg.wss.ms.data.model.OrderItemData;
import de.uniba.dsg.wss.ms.data.model.PaymentData;
import de.uniba.dsg.wss.ms.data.model.ProductData;
import de.uniba.dsg.wss.ms.data.model.StockData;
import de.uniba.dsg.wss.ms.data.model.WarehouseData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Converts a generated data model to a MicroStream data model.
 *
 * @see DataGenerator
 * @author Benedikt Full
 */
public class MsDataConverter implements DataConverter {

  private static final Logger LOG = LogManager.getLogger(MsDataConverter.class);
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

  public MsDataConverter() {
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
    products = convertProducts(generator.getProducts());
    carriers = convertCarriers(generator.getCarriers());
    warehouses = convertWarehouses(generator.getWarehouses(), products, carriers);
    stocks = convertStocks(generator.getWarehouses());
    districts = convertDistricts(generator.getWarehouses());
    employees = convertEmployees(generator.getEmployees(), warehouses);
    customers = convertCustomers(generator.getWarehouses());
    orders = convertOrders(generator.getWarehouses());
    orderItems = convertOrderItems(generator.getWarehouses());
    payments = convertPayments(generator.getWarehouses());
    stopwatch.stop();
    LOG.info("Converted model data to MicroStream data, took {}", stopwatch.getDuration());
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

  private List<ProductData> convertProducts(List<Product> ps) {
    List<ProductData> products = new ArrayList<>(ps.size());
    for (Product p : ps) {
      ProductData product = new ProductData();
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

  private List<CarrierData> convertCarriers(List<Carrier> cs) {
    List<CarrierData> carriers = new ArrayList<>(cs.size());
    for (Carrier c : cs) {
      CarrierData carrier = new CarrierData();
      carrier.setId(c.getId());
      carrier.setName(c.getName());
      carrier.setPhoneNumber(c.getPhoneNumber());
      carrier.setAddress(address(c.getAddress()));
      carriers.add(carrier);
    }
    LOG.debug("Converted {} carriers", carriers.size());
    return carriers;
  }

  private List<WarehouseData> convertWarehouses(
      List<Warehouse> ws, List<ProductData> products, List<CarrierData> carriers) {
    List<WarehouseData> warehouses = new ArrayList<>(ws.size());
    for (Warehouse w : ws) {
      WarehouseData warehouse = new WarehouseData();
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

  private List<StockData> convertStocks(List<Warehouse> ws) {
    List<StockData> stocks =
        ws.stream()
            .flatMap(w -> w.getStocks().stream())
            .map(MsDataConverter::stock)
            .collect(Collectors.toList());
    LOG.debug("Converted {} stocks", stocks.size());
    return stocks;
  }

  private static StockData stock(Stock s) {
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

  private List<DistrictData> convertDistricts(List<Warehouse> ws) {
    List<DistrictData> districts = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        districts.add(district(d, w));
      }
    }
    LOG.debug("Converted {} districts", districts.size());
    return districts;
  }

  private List<EmployeeData> convertEmployees(List<Employee> es, List<WarehouseData> warehouses) {
    List<EmployeeData> employees = new ArrayList<>(es.size());
    for (Employee e : es) {
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
    LOG.debug("Converted {} employees", employees.size());
    return employees;
  }

  private List<CustomerData> convertCustomers(List<Warehouse> ws) {
    List<Customer> cs = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        cs.addAll(d.getCustomers());
      }
    }
    return customers(cs);
  }

  private static List<CustomerData> customers(List<Customer> cs) {
    List<CustomerData> customers = new ArrayList<>(cs.size());
    for (Customer c : cs) {
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
    LOG.debug("Converted {} customers", customers.size());
    return customers;
  }

  private List<OrderData> convertOrders(List<Warehouse> ws) {
    List<Order> os = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        os.addAll(d.getOrders());
      }
    }
    return orders(os);
  }

  private static List<OrderData> orders(List<Order> os) {
    List<OrderData> orders =
        os.parallelStream()
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
    LOG.debug("Converted {} orders", orders.size());
    return orders;
  }

  private List<OrderItemData> convertOrderItems(List<Warehouse> ws) {
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

  private static List<OrderItemData> orderItems(List<OrderItem> ois) {
    List<OrderItemData> orderItems =
        ois.stream()
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
    LOG.debug("Converted {} order items", orderItems.size());
    return orderItems;
  }

  private List<PaymentData> convertPayments(List<Warehouse> ws) {
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

  private static List<PaymentData> payments(List<Payment> ps) {
    List<PaymentData> payments =
        ps.parallelStream()
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
    LOG.debug("Converted {} payments", payments.size());
    return payments;
  }

  private DistrictData district(District d, Warehouse w) {
    DistrictData district = new DistrictData();
    district.setId(d.getId());
    district.setWarehouseId(d.getWarehouse().getId());
    district.setName(d.getName());
    district.setAddress(address(d.getAddress()));
    district.setSalesTax(d.getSalesTax());
    district.setYearToDateBalance(d.getYearToDateBalance());
    return district;
  }

  private static AddressData address(Address a) {
    return new AddressData(
        a.getStreet1(), a.getStreet2(), a.getZipCode(), a.getCity(), a.getState());
  }
}
