package de.uniba.dsg.jpb.server.model;

import de.uniba.dsg.jpb.server.model.id.DistrictId;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@IdClass(DistrictId.class)
public class District {

  @Id private Long id;

  @Id
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "warehouse_id")
  private Warehouse warehouse;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "district", cascade = CascadeType.ALL)
  private List<Customer> customers;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "district", cascade = CascadeType.ALL)
  private List<Order> orders;

  private String name;
  @Embedded private Address address;
  private double salesTax;
  private double yearToDateBalance;
  private long nextOrderId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public Long getNextOrderId() {
    return nextOrderId;
  }

  public void setNextOrderId(long nextOrderId) {
    this.nextOrderId = nextOrderId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    District district = (District) o;
    return id.equals(district.id) && warehouse.equals(district.warehouse);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, warehouse);
  }
}
