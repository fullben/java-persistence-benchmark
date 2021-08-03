package de.uniba.dsg.jpb.server.model;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Describes a single (terminal) line of an order, which in turn specifies an item and quantity in
 * which the item should be ordered. This data is enriched with additional metadata related to the
 * order this line is part of.
 */
@Entity
public class OrderLine {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Order order;

  private int number;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Item item;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Warehouse supplyingWarehouse;

  private LocalDateTime deliveryDate;
  private int quantity;
  private double amount;
  private String distInfo;

  public OrderLine() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public Warehouse getSupplyingWarehouse() {
    return supplyingWarehouse;
  }

  public void setSupplyingWarehouse(Warehouse supplyingWarehouse) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderLine orderLine = (OrderLine) o;
    return id.equals(orderLine.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
