package de.uniba.dsg.jpb.data.transfer.messages;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class NewOrderRequestItem {

  @NotBlank(message = "Product id is required")
  private String productId;

  @NotBlank(message = "Supplying warehouse id is required")
  private String supplyingWarehouseId;

  @Min(value = 1, message = "Quantity must be greater than zero")
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
