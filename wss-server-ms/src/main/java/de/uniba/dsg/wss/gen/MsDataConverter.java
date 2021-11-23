package de.uniba.dsg.wss.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.gen.DataConverter;
import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.data.gen.model.*;
import de.uniba.dsg.wss.data.model.ms.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts a generated data model to a MicroStream data model.
 *
 * @see DataGenerator
 * @author Benedikt Full, Johannes Manner
 */
public class MsDataConverter implements DataConverter {

  private static final Logger LOG = LogManager.getLogger(MsDataConverter.class);

  private Map<String, ProductData> products;
  private Map<String, CarrierData> carriers;
  private Map<String, WarehouseData> warehouses;
  private Map<String, StockData> stocks;
  private Map<String, DistrictData> districts;
  private Map<String, EmployeeData> employees;
  private Map<String, CustomerData> customers;
  private Map<String, OrderData> orders;
  private List<OrderItemData> orderItems;
  private List<PaymentData> payments;

  public Map<String, ProductData> getProducts() {
    return products;
  }

  public Map<String, CarrierData> getCarriers() {
    return carriers;
  }

  public Map<String, WarehouseData> getWarehouses() {
    return warehouses;
  }

  public Map<String, StockData> getStocks() {
    return stocks;
  }

  public Map<String, EmployeeData> getEmployees() {
    return employees;
  }

  public Map<String, CustomerData> getCustomers() {
    return customers;
  }

  public Map<String, OrderData> getOrders() {
    return orders;
  }

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
    warehouses = convertWarehouses(generator.getWarehouses());

    stocks = convertStocks(generator.getWarehouses());
    districts = convertDistricts(generator.getWarehouses());
    employees = convertEmployees(generator.getEmployees());
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

  private Map<String, ProductData> convertProducts(List<Product> ps) {
    Map<String, ProductData> products = new HashMap<>();
    for (Product p : ps) {
      ProductData product = new ProductData(p.getId(),
              p.getImagePath(),
              p.getName(),
              p.getPrice(),
              p.getData());

      products.put(product.getId(), product);
    }
    LOG.debug("Converted {} products", products.size());
    return products;
  }

  private Map<String, CarrierData> convertCarriers(List<Carrier> cs) {
    Map<String, CarrierData> carriers = new HashMap<>();
    for (Carrier c : cs) {
      CarrierData carrier = new CarrierData(c.getId(),
              c.getName(),
              c.getPhoneNumber(),
              address(c.getAddress()));
      carriers.put(carrier.getId(), carrier);
    }
    LOG.debug("Converted {} carriers", carriers.size());
    return carriers;
  }

  private Map<String, WarehouseData> convertWarehouses(List<Warehouse> ws) {
    Map<String, WarehouseData> warehouses = new HashMap<>();
    for (Warehouse w : ws) {
      WarehouseData warehouse = new WarehouseData(w.getId(),
              w.getName(),
              address(w.getAddress()),
              w.getSalesTax());
      // NEW relaxing the concurrency thing here, since at the data generation step, the procedure is implemented single threaded
      warehouse.increaseYearToBalance(w.getYearToDateBalance());
      warehouses.put(warehouse.getId(), warehouse);
    }
    LOG.debug("Converted {} warehouses", warehouses.size());
    return warehouses;
  }

  private Map<String, StockData> convertStocks(List<Warehouse> ws) {
    Map<String, StockData> stocks = new HashMap<>();
    for(Warehouse warehouseBase : ws) {
      WarehouseData warehouse = this.warehouses.get(warehouseBase.getId());
      for(Stock stockBase : warehouseBase.getStocks()){
        // create stock data
        StockData stockData = this.stock(stockBase, warehouse);
        // add stock to warehouse
        warehouse.getStocks().add(stockData);
        stocks.put(stockData.getId(), stockData);
      }
    }

    LOG.debug("Converted {} stocks", stocks.size());
    return stocks;
  }

  private StockData stock(Stock s, WarehouseData warehouse) {
    StockData stock = new StockData(warehouse,
            this.products.get(s.getProduct().getId()),
            s.getQuantity(),
            s.getYearToDateBalance(),
            s.getOrderCount(),
            s.getRemoteCount(),
            s.getData(),
            s.getDist01(),
            s.getDist02(),
            s.getDist03(),
            s.getDist04(),
            s.getDist05(),
            s.getDist06(),
            s.getDist07(),
            s.getDist08(),
            s.getDist09(),
            s.getDist10());
    return stock;
  }

  /**
   * Districts are now also added to the warehouse (bidirectional relationship)
   *
   * @param ws
   * @return
   */
  private Map<String, DistrictData> convertDistricts(List<Warehouse> ws) {
    Map<String, DistrictData> districts = new HashMap<>();
    for (Warehouse w : ws) {
      WarehouseData warehouse = this.warehouses.get(w.getId());
      Map<String, DistrictData> districtsForWarehouse = warehouse.getDistricts();

      for (District d : w.getDistricts()) {
        // referential integrity...
        DistrictData districtData = district(d,warehouse);
        districtsForWarehouse.put(districtData.getId(), districtData);

        districts.put(districtData.getId(), districtData);
      }
    }
    LOG.debug("Converted {} districts", districts.size());
    return districts;
  }

  private Map<String, EmployeeData> convertEmployees(List<Employee> es) {
    Map<String, EmployeeData> employees = new HashMap<>();
    for (Employee e : es) {
      EmployeeData employee = new EmployeeData(e.getId(),
              e.getFirstName(),
              e.getMiddleName(),
              e.getLastName(),
              address(e.getAddress()),
              e.getPhoneNumber(),
              e.getEmail(),
              e.getTitle(),
              e.getUsername(),
              e.getPassword(),
              this.districts.get(e.getDistrict().getId()));

      employees.put(employee.getUsername(), employee);
    }
    LOG.debug("Converted {} employees", employees.size());
    return employees;
  }

  private Map<String, CustomerData> convertCustomers(List<Warehouse> ws) {
    List<Customer> cs = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        cs.addAll(d.getCustomers());
      }
    }
    return customers(cs);
  }

  private Map<String, CustomerData> customers(List<Customer> cs) {
    Map<String, CustomerData> customers = new HashMap<>();
    for (Customer c : cs) {
      CustomerData customer = new CustomerData(c.getId(),
              c.getFirstName(),
              c.getMiddleName(),
              c.getLastName(),
              address(c.getAddress()),
              c.getPhoneNumber(),
              c.getEmail(),
              // referential integrity
              this.districts.get(c.getDistrict().getId()),
              c.getSince(),
              c.getCredit(),
              c.getCreditLimit(),
              c.getDiscount(),
              c.getBalance(),
              c.getYearToDatePayment(),
              c.getPaymentCount(),
              c.getDeliveryCount(),
              c.getData());

      customers.put(customer.getId(), customer);
      // referential integrity
      this.districts.get(customer.getDistrict().getId()).getCustomers().add(customer);
    }
    LOG.debug("Converted {} customers", customers.size());
    return customers;
  }

  private Map<String, OrderData> convertOrders(List<Warehouse> ws) {
    Map<String, OrderData> orders = new HashMap<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        DistrictData district = this.districts.get(d.getId());
        for(Order o : d.getOrders()) {
          OrderData order = new OrderData(o.getId(),
                  district,
                  // referential integrity
                  this.customers.get(o.getCustomer().getId()),
                  // referential integrity
                  this.carriers.get(o.getCarrier() == null ? null : o.getCarrier().getId()),
                  o.getEntryDate(),
                  o.getItemCount(),
                  o.isAllLocal(),
                  o.isFulfilled());

          orders.put(order.getId(), order);
          // referential integrity
          district.getOrders().put(order.getId(), order);
          this.customers.get(order.getCustomerRef().getId()).getOrderRefs().put(order.getId(), order);
        }
      }
    }
    return orders;
  }

  private List<OrderItemData> convertOrderItems(List<Warehouse> ws) {
    List<OrderItemData> ois = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        for (Order o : d.getOrders()) {
          OrderData order = this.orders.get(o.getId());
          for (OrderItem i : o.getItems()) {
            OrderItemData item = new OrderItemData(i.getId(),
                    order,
                    this.products.get(i.getProduct().getId()),
                    this.warehouses.get(i.getSupplyingWarehouse().getId()),
                    i.getNumber(),
                    i.getDeliveryDate(),
                    i.getQuantity(),
                    0, // ok for this initial values
                    i.getAmount(),
                    i.getDistInfo()
            );

            ois.add(item);
            //referential integrity
            order.getItems().add(item);
          }
        }
      }
    }
    return ois;
  }

  private List<PaymentData> convertPayments(List<Warehouse> ws) {
    List<Payment> ps =
            ws.parallelStream()
              .flatMap(w -> w.getDistricts().parallelStream()
                      .flatMap(d -> d.getCustomers().parallelStream()
                        .flatMap(c -> c.getPayments().stream())))
              .collect(Collectors.toList());

    List<PaymentData> payments = new ArrayList<>();
    for (Payment p : ps) {
      PaymentData payment = new PaymentData(p.getId(),
              this.customers.get(p.getCustomer().getId()),
              p.getDate(),
              p.getAmount(),
              p.getData());

      payments.add(payment);
      // referential integrity
      this.customers.get(p.getCustomer().getId()).getPaymentRefs().add(payment);

    }

    return payments;
  }

  private DistrictData district(District d, WarehouseData warehouse) {
    DistrictData district = new DistrictData(
            d.getId(),
            warehouse,
            d.getName(),
            address(d.getAddress()),
            d.getSalesTax(),
            d.getYearToDateBalance()
    );
    return district;
  }

  private static AddressData address(Address a) {
    return new AddressData(
        a.getStreet1(), a.getStreet2(), a.getZipCode(), a.getCity(), a.getState());
  }
}
