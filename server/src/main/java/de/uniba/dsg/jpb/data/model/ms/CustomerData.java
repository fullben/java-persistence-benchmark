package de.uniba.dsg.jpb.data.model.ms;

import java.time.LocalDateTime;
import org.jacis.plugin.objectadapter.cloning.JacisCloneable;

/**
 * A customer of the wholesale supplier.
 *
 * @author Benedikt Full
 */
public class CustomerData extends PersonData implements JacisCloneable<CustomerData> {

  private String warehouseId;
  private String districtId;
  private LocalDateTime since;
  private String credit;
  private double creditLimit;
  private double discount;
  private double balance;
  private double yearToDatePayment;
  private int paymentCount;
  private int deliveryCount;
  private String data;

  public String getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(String warehouseId) {
    checkWritable();
    this.warehouseId = warehouseId;
  }

  public String getDistrictId() {
    return districtId;
  }

  public void setDistrictId(String districtId) {
    checkWritable();
    this.districtId = districtId;
  }

  public LocalDateTime getSince() {
    return since;
  }

  public void setSince(LocalDateTime since) {
    checkWritable();
    this.since = since;
  }

  public String getCredit() {
    return credit;
  }

  public void setCredit(String credit) {
    checkWritable();
    this.credit = credit;
  }

  public double getCreditLimit() {
    return creditLimit;
  }

  public void setCreditLimit(double creditLimit) {
    checkWritable();
    this.creditLimit = creditLimit;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    checkWritable();
    this.discount = discount;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    checkWritable();
    this.balance = balance;
  }

  public double getYearToDatePayment() {
    return yearToDatePayment;
  }

  public void setYearToDatePayment(double yearToDatePayment) {
    checkWritable();
    this.yearToDatePayment = yearToDatePayment;
  }

  public int getPaymentCount() {
    return paymentCount;
  }

  public void setPaymentCount(int paymentCount) {
    checkWritable();
    this.paymentCount = paymentCount;
  }

  public int getDeliveryCount() {
    return deliveryCount;
  }

  public void setDeliveryCount(int deliveryCount) {
    checkWritable();
    this.deliveryCount = deliveryCount;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    checkWritable();
    this.data = data;
  }

  @Override
  public CustomerData clone() {
    return (CustomerData) super.clone();
  }
}
