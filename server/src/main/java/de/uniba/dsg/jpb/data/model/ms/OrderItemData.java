package de.uniba.dsg.jpb.data.model.ms;

import java.time.LocalDateTime;

public class OrderItemData extends BaseData {

  private OrderData order;
  private int number;
  private ProductData product;
  private WarehouseData supplyingWarehouse;
  private LocalDateTime deliveryDate;
  private int quantity;
  private double amount;
  private String distInfo;

  public OrderData getOrder() {
    return order;
  }

  public void setOrder(OrderData order) {
    this.order = order;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public ProductData getProduct() {
    return product;
  }

  public void setProduct(ProductData product) {
    this.product = product;
  }

  public WarehouseData getSupplyingWarehouse() {
    return supplyingWarehouse;
  }

  public void setSupplyingWarehouse(WarehouseData supplyingWarehouse) {
    this.supplyingWarehouse = supplyingWarehouse;
  }

  public LocalDateTime getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(LocalDateTime deliveryDate) {
    this.deliveryDate = deliveryDate;
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

  public String getDistInfo() {
    return distInfo;
  }

  public void setDistInfo(String distInfo) {
    this.distInfo = distInfo;
  }
}
