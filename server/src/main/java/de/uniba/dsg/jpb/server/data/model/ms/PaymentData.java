package de.uniba.dsg.jpb.server.data.model.ms;

import java.time.LocalDateTime;

public class PaymentData {

  private Long id;
  private CustomerData customer;
  private LocalDateTime date;
  private DistrictData district;
  private double amount;
  private String data;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public CustomerData getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerData customer) {
    this.customer = customer;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public DistrictData getDistrict() {
    return district;
  }

  public void setDistrict(DistrictData district) {
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
