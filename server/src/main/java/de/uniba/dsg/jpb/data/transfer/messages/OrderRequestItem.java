package de.uniba.dsg.jpb.data.transfer.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderRequestItem {

  @JsonProperty(required = true)
  private Long productId;

  @JsonProperty(required = true)
  private Long supplyingWarehouseId;

  @JsonProperty(required = true)
  private int quantity;

  public OrderRequestItem() {}

  public OrderRequestItem(OrderRequestItem line) {
    productId = line.productId;
    supplyingWarehouseId = line.supplyingWarehouseId;
    quantity = line.quantity;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getSupplyingWarehouseId() {
    return supplyingWarehouseId;
  }

  public void setSupplyingWarehouseId(Long supplyingWarehouseId) {
    this.supplyingWarehouseId = supplyingWarehouseId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
