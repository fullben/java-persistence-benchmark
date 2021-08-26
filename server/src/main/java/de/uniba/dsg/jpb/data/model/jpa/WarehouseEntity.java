package de.uniba.dsg.jpb.data.model.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "warehouses")
public class WarehouseEntity extends BaseEntity {

  private String name;
  @Embedded private AddressEmbeddable address;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<DistrictEntity> districts;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<StockEntity> stocks;

  private double salesTax;
  private double yearToDateBalance;

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
