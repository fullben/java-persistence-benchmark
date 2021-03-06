package de.uniba.dsg.wss.data.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A warehouse of the wholesale supplier.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 */
@Entity(name = "Warehouse")
@Table(name = "warehouses")
public class WarehouseEntity extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Embedded private AddressEmbeddable address;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse", cascade = CascadeType.ALL)
  private List<DistrictEntity> districts;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse", cascade = CascadeType.ALL)
  private List<StockEntity> stocks;

  private double salesTax;
  private double yearToDateBalance;

  /**
   * Creates a new warehouse entity and initializes all collection fields with empty collections.
   */
  public WarehouseEntity() {
    this.districts = new ArrayList<>();
    this.stocks = new ArrayList<>();
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

  public List<DistrictEntity> getDistricts() {
    return districts;
  }

  public void setDistricts(List<DistrictEntity> districts) {
    this.districts = districts;
  }

  public List<StockEntity> getStocks() {
    return stocks;
  }

  public void setStocks(List<StockEntity> stocks) {
    this.stocks = stocks;
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
