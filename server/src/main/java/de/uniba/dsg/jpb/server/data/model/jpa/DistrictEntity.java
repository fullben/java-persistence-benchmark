package de.uniba.dsg.jpb.server.data.model.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "districts")
public class DistrictEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "warehouse_id")
  @JsonIgnore
  private WarehouseEntity warehouse;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "district", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<CustomerEntity> customers;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "district", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<OrderEntity> orders;

  private String name;
  @Embedded private AddressEmbeddable address;
  private double salesTax;
  private double yearToDateBalance;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DistrictEntity that = (DistrictEntity) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
