package de.uniba.dsg.wss.data.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A district is one of ten areas supplied by a specific {@link WarehouseEntity warehouse}. Each
 * district is administered by a single {@link EmployeeEntity employee} and has 3000 {@link
 * CustomerEntity customers}.
 *
 * @author Benedikt Full
 */
@Entity(name = "District")
@Table(
    name = "districts",
    indexes = {@Index(name = "districts_idx_warehouse_id", columnList = "warehouse_id")})
public class DistrictEntity extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "warehouse_id", nullable = false)
  private WarehouseEntity warehouse;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "district", cascade = CascadeType.ALL)
  private List<CustomerEntity> customers;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "district", cascade = CascadeType.ALL)
  private List<OrderEntity> orders;

  @Column(nullable = false)
  private String name;

  @Embedded private AddressEmbeddable address;
  private double salesTax;
  private double yearToDateBalance;

  public WarehouseEntity getWarehouse() {
    return warehouse;
  }

  public void setWarehouse(WarehouseEntity warehouse) {
    this.warehouse = warehouse;
  }

  public List<CustomerEntity> getCustomers() {
    return customers;
  }

  public void setCustomers(List<CustomerEntity> customers) {
    this.customers = customers;
  }

  public List<OrderEntity> getOrders() {
    return orders;
  }

  public void setOrders(List<OrderEntity> orders) {
    this.orders = orders;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AddressEmbeddable getAddress() {
    return address;
  }

  public void setAddress(AddressEmbeddable address) {
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
