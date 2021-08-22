package de.uniba.dsg.jpb.data.transfer.representations;

public class WarehouseRepresentation {

  private Long id;
  private String name;
  private AddressRepresentation address;
  private double salesTax;
  private double yearToDateBalance;

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

  public AddressRepresentation getAddress() {
    return address;
  }

  public void setAddress(AddressRepresentation address) {
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
