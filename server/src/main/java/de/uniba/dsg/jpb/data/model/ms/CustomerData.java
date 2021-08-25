package de.uniba.dsg.jpb.data.model.ms;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import one.microstream.reference.Lazy;

public class CustomerData extends PersonData {

  private DistrictData district;
  private LocalDateTime since;
  private Lazy<List<PaymentData>> payments;
  private Lazy<List<OrderData>> orders;
  private String credit;
  private double creditLimit;
  private double discount;
  private double balance;
  private double yearToDatePayment;
  private int paymentCount;
  private int deliveryCount;
  private String data;

  public CustomerData() {
    super();
    payments = Lazy.Reference(new ArrayList<>());
    orders = Lazy.Reference(new ArrayList<>());
  }

  public DistrictData getDistrict() {
    return district;
  }

  public void setDistrict(DistrictData district) {
    this.district = district;
  }

  public LocalDateTime getSince() {
    return since;
  }

  public void setSince(LocalDateTime since) {
    this.since = since;
  }

  public List<PaymentData> getPayments() {
    return Lazy.get(payments);
  }

  public void setPayments(List<PaymentData> payments) {
    this.payments = Lazy.Reference(payments);
  }

  public List<OrderData> getOrders() {
    return Lazy.get(orders);
  }

  public void setOrders(List<OrderData> orders) {
    this.orders = Lazy.Reference(orders);
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
