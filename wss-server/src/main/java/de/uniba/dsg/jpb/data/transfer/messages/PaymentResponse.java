package de.uniba.dsg.jpb.data.transfer.messages;

public class PaymentResponse {

  private String warehouseId;
  private String districtId;
  private String customerId;
  private String paymentId;
  private String customerCredit;
  private double customerCreditLimit;
  private double customerDiscount;
  private double customerBalance;
  private double paymentAmount;

  public PaymentResponse(PaymentRequest req) {
    warehouseId = req.getWarehouseId();
    districtId = req.getDistrictId();
    customerId = req.getCustomerId();
    paymentAmount = req.getAmount();
  }

  public String getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(String warehouseId) {
    this.warehouseId = warehouseId;
  }

  public String getDistrictId() {
    return districtId;
  }

  public void setDistrictId(String districtId) {
    this.districtId = districtId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public String getCustomerCredit() {
    return customerCredit;
  }

  public void setCustomerCredit(String customerCredit) {
    this.customerCredit = customerCredit;
  }

  public double getCustomerCreditLimit() {
    return customerCreditLimit;
  }

  public void setCustomerCreditLimit(double customerCreditLimit) {
    this.customerCreditLimit = customerCreditLimit;
  }

  public double getCustomerDiscount() {
    return customerDiscount;
  }

  public void setCustomerDiscount(double customerDiscount) {
    this.customerDiscount = customerDiscount;
  }

  public double getCustomerBalance() {
    return customerBalance;
  }

  public void setCustomerBalance(double customerBalance) {
    this.customerBalance = customerBalance;
  }

  public double getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(double paymentAmount) {
    this.paymentAmount = paymentAmount;
  }
}
