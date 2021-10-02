package de.uniba.dsg.wss.data.model.ms;

import org.jacis.plugin.objectadapter.cloning.JacisCloneable;

/**
 * A district is one of ten areas supplied by a specific {@link WarehouseData warehouse}. Each
 * district is administered by a single {@link EmployeeData employee} and has 3000 {@link
 * CustomerData customers}.
 *
 * @author Benedikt Full
 */
public class DistrictData extends BaseData implements JacisCloneable<DistrictData> {

  private String warehouseId;
  private String name;
  private AddressData address;
  private double salesTax;
  private double yearToDateBalance;

  public String getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(String warehouseId) {
    checkWritable();
    this.warehouseId = warehouseId;
  }

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
  public DistrictData clone() {
    return (DistrictData) super.clone();
  }
}
