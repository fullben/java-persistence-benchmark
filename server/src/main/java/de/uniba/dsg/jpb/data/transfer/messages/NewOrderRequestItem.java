package de.uniba.dsg.jpb.data.transfer.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewOrderRequestItem {

  @JsonProperty(required = true)
  private String productId;

  @JsonProperty(required = true)
  private String supplyingWarehouseId;

  @JsonProperty(required = true)
  private int quantity;

  public NewOrderRequestItem() {}

  public NewOrderRequestItem(NewOrderRequestItem line) {
    productId = line.productId;
    supplyingWarehouseId = line.supplyingWarehouseId;
    quantity = line.quantity;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getSupplyingWarehouseId() {
    return supplyingWarehouseId;
  }

  public void setSupplyingWarehouseId(String supplyingWarehouseId) {
    this.supplyingWarehouseId = supplyingWarehouseId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
