package de.uniba.dsg.wss.data.transfer.representations;

import java.time.LocalDateTime;

public class CustomerRepresentation {

  private String id;
  private String firstName;
  private String middleName;
  private String lastName;
  private AddressRepresentation address;
  private String phoneNumber;
  private String email;
  private DistrictRepresentation district;
  private LocalDateTime since;
  private String credit;
  private double creditLimit;
  private double discount;
  private double balance;
  private double yearToDatePayment;
  private int paymentCount;
  private int deliveryCount;
  private String data;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public AddressRepresentation getAddress() {
    return address;
  }

  public void setAddress(AddressRepresentation address) {
    this.address = address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public DistrictRepresentation getDistrict() {
    return district;
  }

  public void setDistrict(DistrictRepresentation district) {
    this.district = district;
  }

  public LocalDateTime getSince() {
    return since;
  }

  public void setSince(LocalDateTime since) {
    this.since = since;
  }

  public String getCredit() {
    return credit;
  }

  public void setCredit(String credit) {
    this.credit = credit;
  }

  public double getCreditLimit() {
    return creditLimit;
  }

  public void setCreditLimit(double creditLimit) {
    this.creditLimit = creditLimit;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public double getYearToDatePayment() {
    return yearToDatePayment;
  }

  public void setYearToDatePayment(double yearToDatePayment) {
    this.yearToDatePayment = yearToDatePayment;
  }

  public int getPaymentCount() {
    return paymentCount;
  }

  public void setPaymentCount(int paymentCount) {
    this.paymentCount = paymentCount;
  }

  public int getDeliveryCount() {
    return deliveryCount;
  }

  public void setDeliveryCount(int deliveryCount) {
    this.deliveryCount = deliveryCount;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
