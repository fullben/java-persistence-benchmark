package de.uniba.dsg.jpb.data.transfer.messages;

import java.time.LocalDateTime;
import java.util.List;

public class OrderStatusResponse {

  private String warehouseId;
  private String districtId;
  private String customerId;
  private String customerFirstName;
  private String customerMiddleName;
  private String customerLastName;
  private double customerBalance;
  private String orderId;
  private LocalDateTime orderEntryDate;
  private String orderCarrierId;
  private List<OrderItemStatusResponse> itemStatus;

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

  public String getCustomerFirstName() {
    return customerFirstName;
  }

  public void setCustomerFirstName(String customerFirstName) {
    this.customerFirstName = customerFirstName;
  }

  public String getCustomerMiddleName() {
    return customerMiddleName;
  }

  public void setCustomerMiddleName(String customerMiddleName) {
    this.customerMiddleName = customerMiddleName;
  }

  public String getCustomerLastName() {
    return customerLastName;
  }

  public void setCustomerLastName(String customerLastName) {
    this.customerLastName = customerLastName;
  }

  public double getCustomerBalance() {
    return customerBalance;
  }

  public void setCustomerBalance(double customerBalance) {
    this.customerBalance = customerBalance;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public LocalDateTime getOrderEntryDate() {
    return orderEntryDate;
  }

  public void setOrderEntryDate(LocalDateTime orderEntryDate) {
    this.orderEntryDate = orderEntryDate;
  }

  public String getOrderCarrierId() {
    return orderCarrierId;
  }

  public void setOrderCarrierId(String orderCarrierId) {
    this.orderCarrierId = orderCarrierId;
  }

  public List<OrderItemStatusResponse> getItemStatus() {
    return itemStatus;
  }

  public void setItemStatus(List<OrderItemStatusResponse> itemStatus) {
    this.itemStatus = itemStatus;
  }
}
