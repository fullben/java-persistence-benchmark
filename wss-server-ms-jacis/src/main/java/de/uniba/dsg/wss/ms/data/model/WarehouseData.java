package de.uniba.dsg.wss.ms.data.model;

import org.jacis.plugin.objectadapter.cloning.JacisCloneable;

/**
 * A warehouse of the wholesale supplier.
 *
 * @author Benedikt Full
 */
public class WarehouseData extends BaseData implements JacisCloneable<WarehouseData> {

  private String name;
  private AddressData address;
  private double salesTax;
  private double yearToDateBalance;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    checkWritable();
    this.name = name;
  }

  public AddressData getAddress() {
    return address;
  }

  public void setAddress(AddressData address) {
    checkWritable();
    this.address = address;
  }

  public double getSalesTax() {
    return salesTax;
  }

  public void setSalesTax(double salesTax) {
    checkWritable();
    this.salesTax = salesTax;
  }

  public double getYearToDateBalance() {
    return yearToDateBalance;
  }

  public void setYearToDateBalance(double yearToDateBalance) {
    checkWritable();
    this.yearToDateBalance = yearToDateBalance;
  }

  @Override
  public WarehouseData clone() {
    return (WarehouseData) super.clone();
  }
}
