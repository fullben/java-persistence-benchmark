package de.uniba.dsg.wss.data.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A customer of the wholesale supplier.
 *
 * @author Benedikt Full
 */
@Entity(name = "Customer")
@Table(
    name = "customers",
    indexes = {
      @Index(name = "customers_idx_email", columnList = "email"),
      @Index(name = "customers_idx_district_id", columnList = "district_id")
    })
public class CustomerEntity extends PersonEntity {

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private DistrictEntity district;

  @Column(nullable = false)
  private LocalDateTime since;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
  private List<PaymentEntity> payments;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
  private List<OrderEntity> orders;

  public CustomerEntity(){
    this.payments = new ArrayList<>();
    this.orders = new ArrayList<>();
  }

  @Column(nullable = false)
  private String credit;

  private double creditLimit;
  private double discount;
  private double balance;
  private double yearToDatePayment;
  private int paymentCount;
  private int deliveryCount;

  @Lob
  @Column(nullable = false)
  private String data;

  public DistrictEntity getDistrict() {
    return district;
  }

  public void setDistrict(DistrictEntity district) {
    this.district = district;
  }

  public LocalDateTime getSince() {
    return since;
  }

  public void setSince(LocalDateTime since) {
    this.since = since;
  }

  public List<PaymentEntity> getPayments() {
    return payments;
  }

  public void setPayments(List<PaymentEntity> payments) {
    this.payments = payments;
  }

  public List<OrderEntity> getOrders() {
    return orders;
  }

  public void setOrders(List<OrderEntity> orders) {
    this.orders = orders;
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
