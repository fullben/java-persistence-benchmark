package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.EmployeeData;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.ProductData;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class for storing the converted model data produced by {@link MsDataConverter} instances.
 *
 * @author Benedikt Full
 */
public class MsDataModel
    implements DataModel<ProductData, WarehouseData, EmployeeData, CarrierData> {

  private final Map<String, ProductData> products;
  private final Map<String, WarehouseData> warehouses;
  private final Map<String, EmployeeData> employees;
  private final Map<String, StockData> stocks;
  private final Map<String, CarrierData> carriers;
  private final Map<String, CustomerData> customers;
  private final Map<String, OrderData> orders;
  private final Stats stats;

  public MsDataModel(
      Map<String, ProductData> products,
      Map<String, WarehouseData> warehouses,
      Map<String, EmployeeData> employees,
      Map<String, StockData> stocks,
      Map<String, CarrierData> carriers,
      Map<String, CustomerData> customers,
      Map<String, OrderData> orders,
      Stats stats) {
    this.products = products;
    this.warehouses = warehouses;
    this.employees = employees;
    this.stocks = stocks;
    this.carriers = carriers;
    this.customers = customers;
    this.orders = orders;
    this.stats = stats;
  }

  public Map<String, ProductData> getIdsToProducts() {
    return products;
  }

  public Map<String, WarehouseData> getIdsToWarehouses() {
    return warehouses;
  }

  public Map<String, EmployeeData> getIdsToEmployees() {
    return employees;
  }

  public Map<String, StockData> getIdsToStocks() {
    return stocks;
  }

  public Map<String, CarrierData> getIdsToCarriers() {
    return carriers;
  }

  public Map<String, CustomerData> getIdsToCustomers() {
    return customers;
  }

  public Map<String, OrderData> getIdsToOrders() {
    return orders;
  }

  @Override
  public List<ProductData> getProducts() {
    return new ArrayList<>(products.values());
  }

  @Override
  public List<WarehouseData> getWarehouses() {
    return new ArrayList<>(warehouses.values());
  }

  @Override
  public List<EmployeeData> getEmployees() {
    return new ArrayList<>(employees.values());
  }

  @Override
  public List<CarrierData> getCarriers() {
    return new ArrayList<>(carriers.values());
  }

  @Override
  public Stats getStats() {
    return stats;
  }
}
