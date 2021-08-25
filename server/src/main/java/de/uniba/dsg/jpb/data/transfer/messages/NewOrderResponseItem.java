package de.uniba.dsg.jpb.data.transfer.messages;

public class NewOrderResponseItem {

  private Long supplyingWarehouseId;
  private Long itemId;
  private String itemName;
  private double itemPrice;
  private double amount;
  private int quantity;
  private int stockQuantity;
  private String brandGeneric;

  public NewOrderResponseItem() {}

  public Long getSupplyingWarehouseId() {
    return supplyingWarehouseId;
  }

  public void setSupplyingWarehouseId(Long supplyingWarehouseId) {
    this.supplyingWarehouseId = supplyingWarehouseId;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public double getItemPrice() {
    return itemPrice;
  }

  public void setItemPrice(double itemPrice) {
    this.itemPrice = itemPrice;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public int getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(int stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  public String getBrandGeneric() {
    return brandGeneric;
  }

  public void setBrandGeneric(String brandGeneric) {
    this.brandGeneric = brandGeneric;
  }
}
