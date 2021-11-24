package de.uniba.dsg.wss.data.model;

import java.time.LocalDateTime;

/**
 * Defines {@link ProductData product}, quantity, supplying {@link WarehouseData warehouse} and
 * other properties of an individual {@link OrderData order} item.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 */
public class OrderItemData extends BaseData {

  private final OrderData orderRef;
  private final ProductData productRef;
  private final WarehouseData supplyingWarehouseRef;

  private final int number;
  private final int quantity;
  private final int leftQuantityInStock;
  private final String distInfo;
  private final double amount;

  private LocalDateTime deliveryDate;

  public OrderItemData(
      OrderData orderRef,
      ProductData productRef,
      WarehouseData supplyingWarehouseRef,
      int number,
      int quantity,
      int leftQuantityInStock,
      double amount,
      String distInfo) {
    super();
    this.orderRef = orderRef;
    this.productRef = productRef;
    this.supplyingWarehouseRef = supplyingWarehouseRef;
    this.number = number;
    this.quantity = quantity;
    this.leftQuantityInStock = leftQuantityInStock;
    this.amount = amount;
    this.distInfo = distInfo;
  }

  // JPA conversion constructor
  public OrderItemData(
      String id,
      OrderData orderRef,
      ProductData productRef,
      WarehouseData supplyingWarehouseRef,
      int number,
      LocalDateTime deliveryDate,
      int quantity,
      int leftQuantityInStock,
      double amount,
      String distInfo) {
    super(id);
    this.orderRef = orderRef;
    this.productRef = productRef;
    this.supplyingWarehouseRef = supplyingWarehouseRef;
    this.number = number;
    this.deliveryDate = deliveryDate;
    this.quantity = quantity;
    this.leftQuantityInStock = leftQuantityInStock;
    this.amount = amount;
    this.distInfo = distInfo;
  }

  public OrderData getOrderRef() {
    return orderRef;
  }

  public ProductData getProductRef() {
    return productRef;
  }

  public WarehouseData getSupplyingWarehouseRef() {
    return supplyingWarehouseRef;
  }

  public int getNumber() {
    return number;
  }

  public int getQuantity() {
    return quantity;
  }

  public String getDistInfo() {
    return distInfo;
  }

  public int getLeftQuantityInStock() {
    return leftQuantityInStock;
  }

  public double getAmount() {
    return amount;
  }

  public LocalDateTime getDeliveryDate() {
    synchronized (this.id) {
      return deliveryDate;
    }
  }

  public void updateDeliveryDate() {
    synchronized (this.id) {
      this.deliveryDate = LocalDateTime.now();
    }
  }
}
