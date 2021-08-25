package de.uniba.dsg.jpb.data.model.ms;

import java.util.ArrayList;
import java.util.List;
import one.microstream.reference.Lazy;

public class WarehouseData {

  private Long id;
  private String name;
  private AddressData address;
  private Lazy<List<DistrictData>> districts;
  private Lazy<List<StockData>> stocks;
  private double salesTax;
  private double yearToDateBalance;

  public WarehouseData() {
    districts = Lazy.Reference(new ArrayList<>());
    stocks = Lazy.Reference(new ArrayList<>());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public List<DistrictData> getDistricts() {
    return Lazy.get(districts);
  }

  public void setDistricts(List<DistrictData> districts) {
    this.districts = Lazy.Reference(districts);
  }

  public List<StockData> getStocks() {
    return Lazy.get(stocks);
  }

  public void setStocks(List<StockData> stocks) {
    this.stocks = Lazy.Reference(stocks);
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
