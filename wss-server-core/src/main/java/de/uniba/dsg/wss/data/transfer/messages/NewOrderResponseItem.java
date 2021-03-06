package de.uniba.dsg.wss.data.transfer.messages;

public class NewOrderResponseItem {

  private String supplyingWarehouseId;
  private String itemId;
  private String itemName;
  private double itemPrice;
  private double amount;
  private int quantity;
  private int stockQuantity;
  private String brandGeneric;

  public NewOrderResponseItem() {}

  public NewOrderResponseItem(
      String supplyingWarehouseId,
      String itemId,
      String itemName,
      double itemPrice,
      double amount,
      int quantity,
      int stockQuantity,
      String brandGeneric) {
    this.supplyingWarehouseId = supplyingWarehouseId;
    this.itemId = itemId;
    this.itemName = itemName;
    this.itemPrice = itemPrice;
    this.amount = amount;
    this.quantity = quantity;
    this.stockQuantity = stockQuantity;
    this.brandGeneric = brandGeneric;
  }

  public String getSupplyingWarehouseId() {
    return supplyingWarehouseId;
  }

  public void setSupplyingWarehouseId(String supplyingWarehouseId) {
    this.supplyingWarehouseId = supplyingWarehouseId;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
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
