package de.uniba.dsg.wss.data.transfer.messages;

import java.time.LocalDateTime;

public class OrderItemStatusResponse {

  private String supplyingWarehouseId;
  private String productId;
  private int quantity;
  private double amount;
  private LocalDateTime deliveryDate;

  public OrderItemStatusResponse() {}

  public OrderItemStatusResponse(
      String supplyingWarehouseId,
      String productId,
      int quantity,
      double amount,
      LocalDateTime deliveryDate) {
    this.supplyingWarehouseId = supplyingWarehouseId;
    this.productId = productId;
    this.quantity = quantity;
    this.amount = amount;
    this.deliveryDate = deliveryDate;
  }

  public String getSupplyingWarehouseId() {
    return supplyingWarehouseId;
  }

  public void setSupplyingWarehouseId(String supplyingWarehouseId) {
    this.supplyingWarehouseId = supplyingWarehouseId;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public LocalDateTime getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(LocalDateTime deliveryDate) {
    this.deliveryDate = deliveryDate;
  }
}
