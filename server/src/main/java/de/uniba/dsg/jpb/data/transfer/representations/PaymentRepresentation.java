package de.uniba.dsg.jpb.data.transfer.representations;

import java.time.LocalDateTime;

public class PaymentRepresentation {

  private Long id;
  private CustomerRepresentation customer;
  private LocalDateTime date;
  private DistrictRepresentation district;
  private double amount;
  private String data;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public CustomerRepresentation getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerRepresentation customer) {
    this.customer = customer;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public DistrictRepresentation getDistrict() {
    return district;
  }

  public void setDistrict(DistrictRepresentation district) {
    this.district = district;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
