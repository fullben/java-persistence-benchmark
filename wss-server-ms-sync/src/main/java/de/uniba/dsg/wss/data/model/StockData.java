package de.uniba.dsg.wss.data.model;

/**
 * The available amount of a specific {@link ProductData product} at some {@link WarehouseData
 * warehouse}.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 */
public class StockData extends BaseData {

  public static int increaseQuantity = 100;
  private final WarehouseData warehouseRef;
  private final ProductData productRef;
  private int quantity;
  private double yearToDateBalance;
  private int orderCount;
  private int remoteCount;
  private final String data;
  private final String dist01;
  private final String dist02;
  private final String dist03;
  private final String dist04;
  private final String dist05;
  private final String dist06;
  private final String dist07;
  private final String dist08;
  private final String dist09;
  private final String dist10;

  public StockData(
      WarehouseData warehouseRef,
      ProductData productRef,
      int quantity,
      double yearToDateBalance,
      int orderCount,
      int remoteCount,
      String data,
      String dist01,
      String dist02,
      String dist03,
      String dist04,
      String dist05,
      String dist06,
      String dist07,
      String dist08,
      String dist09,
      String dist10) {
    // optimization
    super(warehouseRef.getId() + productRef.getId());
    this.warehouseRef = warehouseRef;
    this.productRef = productRef;
    this.quantity = quantity;
    this.yearToDateBalance = yearToDateBalance;
    this.orderCount = orderCount;
    this.remoteCount = remoteCount;
    this.data = data;
    this.dist01 = dist01;
    this.dist02 = dist02;
    this.dist03 = dist03;
    this.dist04 = dist04;
    this.dist05 = dist05;
    this.dist06 = dist06;
    this.dist07 = dist07;
    this.dist08 = dist08;
    this.dist09 = dist09;
    this.dist10 = dist10;
  }

  public WarehouseData getWarehouseRef() {
    return warehouseRef;
  }

  public ProductData getProductRef() {
    return productRef;
  }

  public int getQuantity() {
    synchronized (this.id) {
      return quantity;
    }
  }

  public double getYearToDateBalance() {
    synchronized (this.id) {
      return yearToDateBalance;
    }
  }

  public int getOrderCount() {
    synchronized (this.id) {
      return orderCount;
    }
  }

  public int getRemoteCount() {
    synchronized (this.id) {
      return remoteCount;
    }
  }

  public String getData() {
    return data;
  }

  public String getDist01() {
    return dist01;
  }

  public String getDist02() {
    return dist02;
  }

  public String getDist03() {
    return dist03;
  }

  public String getDist04() {
    return dist04;
  }

  public String getDist05() {
    return dist05;
  }

  public String getDist06() {
    return dist06;
  }

  public String getDist07() {
    return dist07;
  }

  public String getDist08() {
    return dist08;
  }

  public String getDist09() {
    return dist09;
  }

  public String getDist10() {
    return dist10;
  }

  public boolean reduceQuantity(int quantity) {
    synchronized (this.id) {
      if (this.quantity < quantity) {
        // avoid permanent out-of-stock scenarios, but let this order fail and retry
        // replace the NewOrderService#determineNewStockQuantity functionality
        this.quantity += increaseQuantity;
        return false;
      }
      this.quantity -= quantity;
      this.yearToDateBalance += quantity;
      this.orderCount++;
      return true;
    }
  }

  public void undoReduceQuantityOperation(int quantity) {
    synchronized (this.id) {
      this.quantity += quantity;
      this.yearToDateBalance -= quantity;
      this.orderCount--;
    }
  }
}
