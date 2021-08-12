package de.uniba.dsg.jpb.server.data.model.ms;

import java.util.List;
import one.microstream.reference.Lazy;

public class DistrictData {

  private Long id;
  private WarehouseData warehouse;
  private Lazy<List<CustomerData>> customers;
  private Lazy<List<OrderData>> orders;
  private String name;
  private AddressData address;
  private double salesTax;
  private double yearToDateBalance;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
