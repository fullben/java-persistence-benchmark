package de.uniba.dsg.jpb.server.messages;

import java.time.LocalDateTime;
import java.util.List;

public class OrderStatusResponse {

  private Long warehouseId;
  private Long districtId;
  private Long customerId;
  private String customerFirstName;
  private String customerMiddleName;
  private String customerLastName;
  private double customerBalance;
  private Long orderId;
  private LocalDateTime orderEntryDate;
  private Long orderCarrierId;
  private List<OrderItemStatusResponse> itemStatus;

  public Long getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(Long warehouseId) {
    this.warehouseId = warehouseId;
  }

  public Long getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Long districtId) {
    this.districtId = districtId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
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

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public LocalDateTime getOrderEntryDate() {
    return orderEntryDate;
  }

  public void setOrderEntryDate(LocalDateTime orderEntryDate) {
    this.orderEntryDate = orderEntryDate;
  }

  public Long getOrderCarrierId() {
    return orderCarrierId;
  }

  public void setOrderCarrierId(Long orderCarrierId) {
    this.orderCarrierId = orderCarrierId;
  }

  public List<OrderItemStatusResponse> getItemStatus() {
    return itemStatus;
  }

  public void setItemStatus(List<OrderItemStatusResponse> itemStatus) {
    this.itemStatus = itemStatus;
  }
}
