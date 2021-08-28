package de.uniba.dsg.jpb.data.model.ms;

import java.util.ArrayList;
import java.util.List;
import one.microstream.reference.Lazy;

/**
 * A district is one of ten areas supplied by a specific {@link WarehouseData Warehouse}. Each
 * district is administered by a single {@link EmployeeData Employee} and has 3000 {@link
 * CustomerData Customers}.
 *
 * @author Benedikt Full
 */
public class DistrictData extends BaseData {

  private WarehouseData warehouse;
  private Lazy<List<CustomerData>> customers;
  private Lazy<List<OrderData>> orders;
  private String name;
  private AddressData address;
  private double salesTax;
  private double yearToDateBalance;

  public DistrictData() {
    super();
    customers = Lazy.Reference(new ArrayList<>());
    orders = Lazy.Reference(new ArrayList<>());
  }

  public WarehouseData getWarehouse() {
    return warehouse;
  }

  public void setWarehouse(WarehouseData warehouse) {
    this.warehouse = warehouse;
  }

  public List<CustomerData> getCustomers() {
    return Lazy.get(customers);
  }

  public void setCustomers(List<CustomerData> customers) {
    this.customers = Lazy.Reference(customers);
  }

  public List<OrderData> getOrders() {
    return Lazy.get(orders);
  }

  public void setOrders(List<OrderData> orders) {
    this.orders = Lazy.Reference(orders);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AddressData getAddress() {
    return address;
  }

  public void setAddress(AddressData address) {
    this.address = address;
  }

  public double getSalesTax() {
    return salesTax;
  }

  public void setSalesTax(double salesTax) {
    this.salesTax = salesTax;
  }

  public double getYearToDateBalance() {
    return yearToDateBalance;
  }

  public void setYearToDateBalance(double yearToDateBalance) {
    this.yearToDateBalance = yearToDateBalance;
  }
}
