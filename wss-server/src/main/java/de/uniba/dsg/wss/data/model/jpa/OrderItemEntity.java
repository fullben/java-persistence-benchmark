package de.uniba.dsg.wss.data.model.jpa;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Defines {@link ProductEntity Product}, quantity, supplying {@link WarehouseEntity Warehouse} and
 * other properties of an individual {@link OrderEntity Order} item.
 *
 * @author Benedikt Full
 */
@Entity
@Table(name = "orderitems")
public class OrderItemEntity extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private OrderEntity order;

  private int number;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private ProductEntity product;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private WarehouseEntity supplyingWarehouse;

  private LocalDateTime deliveryDate;
  private int quantity;
  private double amount;
  private String distInfo;

  public OrderEntity getOrder() {
    return order;
  }

  public void setOrder(OrderEntity order) {
    this.order = order;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public ProductEntity getProduct() {
    return product;
  }

  public void setProduct(ProductEntity product) {
    this.product = product;
  }

  public WarehouseEntity getSupplyingWarehouse() {
    return supplyingWarehouse;
  }

  public void setSupplyingWarehouse(WarehouseEntity supplyingWarehouse) {
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
