package de.uniba.dsg.wss.data.transfer.messages;

import java.time.LocalDateTime;
import java.util.List;

public class NewOrderResponse {

  private String warehouseId;
  private String districtId;
  private String customerId;
  private String orderId;
  private List<NewOrderResponseItem> orderItems;
  private int orderItemCount;
  private LocalDateTime orderTimestamp;
  private String customerLastName;
  private String customerCredit;
  private double customerDiscount;
  private double warehouseSalesTax;
  private double districtSalesTax;
  private double totalAmount;
  private String message;

  public NewOrderResponse(NewOrderRequest request) {
    warehouseId = request.getWarehouseId();
    districtId = request.getDistrictId();
    customerId = request.getCustomerId();
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

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public List<NewOrderResponseItem> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<NewOrderResponseItem> orderItems) {
    this.orderItems = orderItems;
  }

  public int getOrderItemCount() {
    return orderItemCount;
  }

  public void setOrderItemCount(int orderItemCount) {
    this.orderItemCount = orderItemCount;
  }

  public LocalDateTime getOrderTimestamp() {
    return orderTimestamp;
  }

  public void setOrderTimestamp(LocalDateTime orderTimestamp) {
    this.orderTimestamp = orderTimestamp;
  }

  public String getCustomerLastName() {
    return customerLastName;
  }

  public void setCustomerLastName(String customerLastName) {
    this.customerLastName = customerLastName;
  }

  public String getCustomerCredit() {
    return customerCredit;
  }

  public void setCustomerCredit(String customerCredit) {
    this.customerCredit = customerCredit;
  }

  public double getCustomerDiscount() {
    return customerDiscount;
  }

  public void setCustomerDiscount(double customerDiscount) {
    this.customerDiscount = customerDiscount;
  }

  public double getWarehouseSalesTax() {
    return warehouseSalesTax;
  }

  public void setWarehouseSalesTax(double warehouseSalesTax) {
    this.warehouseSalesTax = warehouseSalesTax;
  }

  public double getDistrictSalesTax() {
    return districtSalesTax;
  }

  public void setDistrictSalesTax(double districtSalesTax) {
    this.districtSalesTax = districtSalesTax;
  }

  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
