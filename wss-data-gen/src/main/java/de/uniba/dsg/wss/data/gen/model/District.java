package de.uniba.dsg.wss.data.gen.model;

import java.util.List;

/**
 * A district is one of ten areas supplied by a specific {@link Warehouse warehouse}. Each district
 * is administered by a single {@link Employee employee} and has 3000 {@link Customer customers}.
 *
 * @author Benedikt Full
 */
public class District extends Base {

  private Warehouse warehouse;
  private List<Customer> customers;
  private List<Order> orders;
  private String name;
  private Address address;
  private double salesTax;
  private double yearToDateBalance;

  public Warehouse getWarehouse() {
    return warehouse;
  }

  public void setWarehouse(Warehouse warehouse) {
    this.warehouse = warehouse;
  }

  public List<Customer> getCustomers() {
    return customers;
  }

  public void setCustomers(List<Customer> customers) {
    this.customers = customers;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
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
