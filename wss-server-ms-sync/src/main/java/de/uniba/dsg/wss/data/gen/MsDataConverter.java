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
import de.uniba.dsg.wss.data.model.AddressData;
import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.EmployeeData;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.OrderItemData;
import de.uniba.dsg.wss.data.model.PaymentData;
import de.uniba.dsg.wss.data.model.ProductData;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Converts a generated data model to a MicroStream data model.
 *
 * @see DataGenerator
 * @author Benedikt Full
 * @author Johannes Manner
 */
public class MsDataConverter
    implements DataConverter<ProductData, WarehouseData, EmployeeData, CarrierData> {

  private static final Logger LOG = LogManager.getLogger(MsDataConverter.class);

  @Override
  public MsDataModel convert(DataModel<Product, Warehouse, Employee, Carrier> model) {
    // Create model objects by converting provided template
    Stopwatch stopwatch = new Stopwatch().start();
    Map<String, ProductData> products = convertProducts(model.getProducts());
    Map<String, CarrierData> carriers = convertCarriers(model.getCarriers());
    Map<String, WarehouseData> warehouses = convertWarehouses(model.getWarehouses());
    Map<String, StockData> stocks = convertStocks(model.getWarehouses(), warehouses, products);
    Map<String, DistrictData> districts = convertDistricts(model.getWarehouses(), warehouses);
    Map<String, EmployeeData> employees = convertEmployees(model.getEmployees(), districts);
    Map<String, CustomerData> customers = convertCustomers(model.getWarehouses(), districts);
    Map<String, OrderData> orders =
        convertOrders(model.getWarehouses(), districts, customers, carriers);
    convertOrderItems(model.getWarehouses(), warehouses, products, orders);
    convertPayments(model.getWarehouses(), customers);
    stopwatch.stop();

    // Create summary data
    Stats stats = new Stats();
    stats.setTotalModelObjectCount(model.getStats().getTotalModelObjectCount());
    stats.setDurationMillis(stopwatch.getDurationMillis());
    stats.setDuration(stopwatch.getDuration());

    // Wrap everything in model instance
    MsDataModel generatedModel =
        new MsDataModel(
            products, warehouses, employees, stocks, carriers, customers, orders, stats);

    LOG.info("Converted model data to MicroStream data, took {}", stopwatch.getDuration());

    return generatedModel;
  }

  private Map<String, ProductData> convertProducts(List<Product> ps) {
    Map<String, ProductData> products = new HashMap<>();
    for (Product p : ps) {
      ProductData product =
          new ProductData(p.getId(), p.getImagePath(), p.getName(), p.getPrice(), p.getData());
      products.put(product.getId(), product);
    }
    LOG.debug("Converted {} products", products.size());
    return products;
  }

  private Map<String, CarrierData> convertCarriers(List<Carrier> cs) {
    Map<String, CarrierData> carriers = new HashMap<>();
    for (Carrier c : cs) {
      CarrierData carrier =
          new CarrierData(c.getId(), c.getName(), c.getPhoneNumber(), address(c.getAddress()));
      carriers.put(carrier.getId(), carrier);
    }
    LOG.debug("Converted {} carriers", carriers.size());
    return carriers;
  }

  private Map<String, WarehouseData> convertWarehouses(List<Warehouse> ws) {
    Map<String, WarehouseData> warehouses = new HashMap<>();
    for (Warehouse w : ws) {
      WarehouseData warehouse =
          new WarehouseData(w.getId(), w.getName(), address(w.getAddress()), w.getSalesTax());
      // NEW relaxing the concurrency thing here, since at the data generation step, the procedure
      // is implemented single threaded
      warehouse.increaseYearToBalance(w.getYearToDateBalance());
      warehouses.put(warehouse.getId(), warehouse);
    }
    LOG.debug("Converted {} warehouses", warehouses.size());
    return warehouses;
  }

  private Map<String, StockData> convertStocks(
      List<Warehouse> ws,
      Map<String, WarehouseData> warehouses,
      Map<String, ProductData> products) {
    Map<String, StockData> stocks = new HashMap<>();
    for (Warehouse warehouseBase : ws) {
      WarehouseData warehouse = warehouses.get(warehouseBase.getId());
      for (Stock stockBase : warehouseBase.getStocks()) {
        // create stock data
        StockData stockData = stock(stockBase, warehouse, products);
        // add stock to warehouse
        warehouse.getStocks().add(stockData);
        stocks.put(stockData.getId(), stockData);
      }
    }

    LOG.debug("Converted {} stocks", stocks.size());
    return stocks;
  }

  private StockData stock(Stock s, WarehouseData warehouse, Map<String, ProductData> products) {
    return new StockData(
        warehouse,
        products.get(s.getProduct().getId()),
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
  }

  /**
   * Districts are now also added to the warehouse (bidirectional relationship)
   *
   * @param ws warehouses to be converted
   * @param warehouses the already converted warehouses
   * @return a map with district ids as keys and districts as values
   */
  private Map<String, DistrictData> convertDistricts(
      List<Warehouse> ws, Map<String, WarehouseData> warehouses) {
    Map<String, DistrictData> districts = new HashMap<>();
    for (Warehouse w : ws) {
      WarehouseData warehouse = warehouses.get(w.getId());
      Map<String, DistrictData> districtsForWarehouse = warehouse.getDistricts();

      for (District d : w.getDistricts()) {
        // referential integrity...
        DistrictData districtData = district(d, warehouse);
        districtsForWarehouse.put(districtData.getId(), districtData);

        districts.put(districtData.getId(), districtData);
      }
    }
    LOG.debug("Converted {} districts", districts.size());
    return districts;
  }

  private Map<String, EmployeeData> convertEmployees(
      List<Employee> es, Map<String, DistrictData> districts) {
    Map<String, EmployeeData> employees = new HashMap<>();
    for (Employee e : es) {
      EmployeeData employee =
          new EmployeeData(
              e.getId(),
              e.getFirstName(),
              e.getMiddleName(),
              e.getLastName(),
              address(e.getAddress()),
              e.getPhoneNumber(),
              e.getEmail(),
              e.getTitle(),
              e.getUsername(),
              e.getPassword(),
              districts.get(e.getDistrict().getId()));

      employees.put(employee.getUsername(), employee);
    }
    LOG.debug("Converted {} employees", employees.size());
    return employees;
  }

  private Map<String, CustomerData> convertCustomers(
      List<Warehouse> ws, Map<String, DistrictData> districts) {
    List<Customer> cs = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        cs.addAll(d.getCustomers());
      }
    }
    return customers(cs, districts);
  }

  private Map<String, CustomerData> customers(
      List<Customer> cs, Map<String, DistrictData> districts) {
    Map<String, CustomerData> customers = new HashMap<>();
    for (Customer c : cs) {
      CustomerData customer =
          new CustomerData(
              c.getId(),
              c.getFirstName(),
              c.getMiddleName(),
              c.getLastName(),
              address(c.getAddress()),
              c.getPhoneNumber(),
              c.getEmail(),
              // referential integrity
              districts.get(c.getDistrict().getId()),
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
      districts.get(customer.getDistrict().getId()).getCustomers().add(customer);
    }
    LOG.debug("Converted {} customers", customers.size());
    return customers;
  }

  private Map<String, OrderData> convertOrders(
      List<Warehouse> ws,
      Map<String, DistrictData> districts,
      Map<String, CustomerData> customers,
      Map<String, CarrierData> carriers) {
    Map<String, OrderData> orders = new HashMap<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        DistrictData district = districts.get(d.getId());
        for (Order o : d.getOrders()) {
          OrderData order =
              new OrderData(
                  o.getId(),
                  district,
                  // referential integrity
                  customers.get(o.getCustomer().getId()),
                  // referential integrity
                  carriers.get(o.getCarrier() == null ? null : o.getCarrier().getId()),
                  o.getEntryDate(),
                  o.getItemCount(),
                  o.isAllLocal(),
                  o.isFulfilled());

          orders.put(order.getId(), order);
          // referential integrity
          district.getOrders().put(order.getId(), order);
          customers.get(order.getCustomerRef().getId()).getOrderRefs().put(order.getId(), order);
        }
      }
    }
    return orders;
  }

  private List<OrderItemData> convertOrderItems(
      List<Warehouse> ws,
      Map<String, WarehouseData> warehouses,
      Map<String, ProductData> products,
      Map<String, OrderData> orders) {
    List<OrderItemData> ois = new ArrayList<>();
    for (Warehouse w : ws) {
      for (District d : w.getDistricts()) {
        for (Order o : d.getOrders()) {
          OrderData order = orders.get(o.getId());
          for (OrderItem i : o.getItems()) {
            OrderItemData item =
                new OrderItemData(
                    i.getId(),
                    order,
                    products.get(i.getProduct().getId()),
                    warehouses.get(i.getSupplyingWarehouse().getId()),
                    i.getNumber(),
                    i.getDeliveryDate(),
                    i.getQuantity(),
                    0, // ok for this initial values
                    i.getAmount(),
                    i.getDistInfo());

            ois.add(item);
            // referential integrity
            order.getItems().add(item);
          }
        }
      }
    }
    return ois;
  }

  private List<PaymentData> convertPayments(
      List<Warehouse> ws, Map<String, CustomerData> customers) {
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

    List<PaymentData> payments = new ArrayList<>();
    for (Payment p : ps) {
      PaymentData payment =
          new PaymentData(
              p.getId(),
              customers.get(p.getCustomer().getId()),
              p.getDate(),
              p.getAmount(),
              p.getData());

      payments.add(payment);
      // referential integrity
      customers.get(p.getCustomer().getId()).getPaymentRefs().add(payment);
    }

    return payments;
  }

  private DistrictData district(District d, WarehouseData warehouse) {
    return new DistrictData(
        d.getId(),
        warehouse,
        d.getName(),
        address(d.getAddress()),
        d.getSalesTax(),
        d.getYearToDateBalance());
  }

  private static AddressData address(Address a) {
    return new AddressData(
        a.getStreet1(), a.getStreet2(), a.getZipCode(), a.getCity(), a.getState());
  }
}
