package de.uniba.dsg.wss.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A warehouse of the wholesale supplier.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 */
public class WarehouseData extends BaseData {

  private final String name;
  private final AddressData address;
  private final double salesTax;
  private double yearToDateBalance;
  private final Map<String, DistrictData> districtRefs;
  private final List<StockData> stockRefs;

  public WarehouseData(String id, String name, AddressData address, double salesTax) {
    super(id);
    this.name = name;
    this.address = address;
    this.salesTax = salesTax;
    yearToDateBalance = 0.0;
    districtRefs = new HashMap<>();
    stockRefs = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public AddressData getAddress() {
    return address;
  }

  public double getSalesTax() {
    return salesTax;
  }

  public Map<String, DistrictData> getDistricts() {
    return this.districtRefs;
  }

  public List<StockData> getStocks() {
    return this.stockRefs;
  }

  public void increaseYearToBalance(double amount) {
    synchronized (this.id) {
      this.yearToDateBalance += amount;
    }
  }

  public double getYearToDateBalance() {
    synchronized (this.id) {
      return yearToDateBalance;
    }
  }
}
