package de.uniba.dsg.jpb.server.messages;

public class OrderRequestItem {

  private Long itemId;
  private Long supplyingWarehouseId;
  private int quantity;

  public OrderRequestItem() {}

  public OrderRequestItem(OrderRequestItem line) {
    itemId = line.itemId;
    supplyingWarehouseId = line.supplyingWarehouseId;
    quantity = line.quantity;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public Long getSupplyingWarehouseId() {
    return supplyingWarehouseId;
  }

  public void setSupplyingWarehouseId(
      Long supplyingWarehouseId) {
    this.supplyingWarehouseId = supplyingWarehouseId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
