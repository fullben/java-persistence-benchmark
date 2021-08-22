package de.uniba.dsg.jpb.data.transfer.representations;

import java.time.LocalDateTime;

public class OrderItemRepresentation {

  private Long id;
  private OrderRepresentation order;
  private int number;
  private ProductRepresentation product;
  private WarehouseRepresentation supplyingWarehouse;
  private LocalDateTime deliveryDate;
  private int quantity;
  private double amount;
  private String distInfo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public OrderRepresentation getOrder() {
    return order;
  }

  public void setOrder(OrderRepresentation order) {
    this.order = order;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public ProductRepresentation getProduct() {
    return product;
  }

  public void setProduct(ProductRepresentation product) {
    this.product = product;
  }

  public WarehouseRepresentation getSupplyingWarehouse() {
    return supplyingWarehouse;
  }

  public void setSupplyingWarehouse(WarehouseRepresentation supplyingWarehouse) {
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
