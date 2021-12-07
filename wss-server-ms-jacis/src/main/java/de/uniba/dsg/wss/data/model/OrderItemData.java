package de.uniba.dsg.wss.data.model;

import java.time.LocalDateTime;
import org.jacis.plugin.objectadapter.cloning.JacisCloneable;

/**
 * Defines {@link ProductData product}, quantity, supplying {@link WarehouseData warehouse} and
 * other properties of an individual {@link OrderData order} item.
 *
 * @author Benedikt Full
 */
public class OrderItemData extends BaseData implements JacisCloneable<OrderItemData> {

  private String orderId;
  private int number;
  private String productId;
  private String supplyingWarehouseId;
  private LocalDateTime deliveryDate;
  private int quantity;
  private double amount;
  private String distInfo;

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    checkWritable();
    this.orderId = orderId;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    checkWritable();
    this.number = number;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    checkWritable();
    this.productId = productId;
  }

  public String getSupplyingWarehouseId() {
    return supplyingWarehouseId;
  }

  public void setSupplyingWarehouseId(String supplyingWarehouseId) {
    checkWritable();
    this.supplyingWarehouseId = supplyingWarehouseId;
  }

  public LocalDateTime getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(LocalDateTime deliveryDate) {
    checkWritable();
    this.deliveryDate = deliveryDate;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    checkWritable();
    this.quantity = quantity;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    checkWritable();
    this.amount = amount;
  }

  public String getDistInfo() {
    return distInfo;
  }

  public void setDistInfo(String distInfo) {
    checkWritable();
    this.distInfo = distInfo;
  }

  @Override
  public OrderItemData clone() {
    return (OrderItemData) super.clone();
  }
}
