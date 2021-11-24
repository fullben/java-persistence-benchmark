package de.uniba.dsg.wss.data.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A customer of the wholesale supplier.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 */
public class CustomerData extends PersonData {

  private final DistrictData districtRef;
  private final Map<String, OrderData> orderRefs;
  private final List<PaymentData> paymentRefs;

  private final LocalDateTime since;
  private final String credit;
  private final double creditLimit;
  private final double discount;

  private int deliveryCount;
  private String data;
  private double balance;
  private double yearToDatePayment;
  private int paymentCount;

  public CustomerData(
      String id,
      String firstName,
      String middleName,
      String lastName,
      AddressData addressData,
      String phoneNumer,
      String mail,
      DistrictData districtRef,
      LocalDateTime since,
      String credit,
      double creditLimit,
      double discount,
      double balance,
      double yearToDatePayment,
      int paymentCount,
      int deliveryCount,
      String data) {
    super(id, firstName, middleName, lastName, addressData, phoneNumer, mail);
    this.districtRef = districtRef;
    this.since = since;
    this.credit = credit;
    this.creditLimit = creditLimit;
    this.discount = discount;
    this.balance = balance;
    this.yearToDatePayment = yearToDatePayment;
    this.paymentCount = paymentCount;
    this.deliveryCount = deliveryCount;
    this.data = data;
    this.orderRefs = new ConcurrentHashMap<>();
    this.paymentRefs = new ArrayList<>();
  }

  /**
   * Creates a shallow copy of the provided customer.
   *
   * @param customer a customer, must not be {@code null}
   */
  public CustomerData(CustomerData customer) {
    super(
        customer.getId(),
        customer.getFirstName(),
        customer.getMiddleName(),
        customer.getLastName(),
        customer.getAddress(),
        customer.getPhoneNumber(),
        customer.getEmail());
    this.districtRef = customer.districtRef;
    this.since = customer.since;
    this.credit = customer.credit;
    this.creditLimit = customer.creditLimit;
    this.discount = customer.discount;
    this.balance = customer.balance;
    this.yearToDatePayment = customer.yearToDatePayment;
    this.paymentCount = customer.paymentCount;
    this.deliveryCount = customer.deliveryCount;
    this.data = customer.data;
    this.orderRefs = null;
    this.paymentRefs = null;
  }

  public DistrictData getDistrict() {
    return this.districtRef;
  }

  public LocalDateTime getSince() {
    return since;
  }

  public String getCredit() {
    return credit;
  }

  public double getCreditLimit() {
    return creditLimit;
  }

  public double getDiscount() {
    return discount;
  }

  public Map<String, OrderData> getOrderRefs() {
    return this.orderRefs;
  }

  public List<PaymentData> getPaymentRefs() {
    return this.paymentRefs;
  }

  public void decreaseBalance(double amount) {
    synchronized (this.id) {
      balance -= amount;
    }
  }

  public void increaseBalance(double amount) {
    synchronized (this.id) {
      balance += amount;
    }
  }

  public double getBalance() {
    synchronized (this.id) {
      return balance;
    }
  }

  public void increaseYearToBalance(double amount) {
    synchronized (this.id) {
      this.yearToDatePayment += amount;
    }
  }

  public double getYearToDatePayment() {
    synchronized (this.id) {
      return yearToDatePayment;
    }
  }

  public void increasePaymentCount() {
    synchronized (this.id) {
      this.paymentCount++;
    }
  }

  public int getPaymentCount() {
    synchronized (this.id) {
      return paymentCount;
    }
  }

  public void increaseDeliveryCount() {
    synchronized (this.id) {
      this.deliveryCount++;
    }
  }

  public int getDeliveryCount() {
    synchronized (this.id) {
      return deliveryCount;
    }
  }

  public void updateData(String buildNewCustomerData) {
    synchronized (this.id) {
      this.data = buildNewCustomerData;
    }
  }

  public String getData() {
    synchronized (this.id) {
      return data;
    }
  }
}
