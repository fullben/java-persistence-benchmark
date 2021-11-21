package de.uniba.dsg.wss.data.model;

import org.jacis.plugin.objectadapter.cloning.JacisCloneable;

/**
 * The available amount of a specific {@link ProductData product} at some {@link WarehouseData
 * warehouse}.
 *
 * @author Benedikt Full
 */
public class StockData extends BaseData implements JacisCloneable<StockData> {

  private String productId;
  private int quantity;
  private String warehouseId;
  private double yearToDateBalance;
  private int orderCount;
  private int remoteCount;
  private String data;
  private String dist01;
  private String dist02;
  private String dist03;
  private String dist04;
  private String dist05;
  private String dist06;
  private String dist07;
  private String dist08;
  private String dist09;
  private String dist10;

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    checkWritable();
    this.productId = productId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    checkWritable();
    this.quantity = quantity;
  }

  public String getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(String warehouseId) {
    checkWritable();
    this.warehouseId = warehouseId;
  }

  public double getYearToDateBalance() {
    return yearToDateBalance;
  }

  public void setYearToDateBalance(double yearToDateBalance) {
    checkWritable();
    this.yearToDateBalance = yearToDateBalance;
  }

  public int getOrderCount() {
    return orderCount;
  }

  public void setOrderCount(int orderCount) {
    checkWritable();
    this.orderCount = orderCount;
  }

  public int getRemoteCount() {
    return remoteCount;
  }

  public void setRemoteCount(int remoteCount) {
    checkWritable();
    this.remoteCount = remoteCount;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    checkWritable();
    this.data = data;
  }

  public String getDist01() {
    return dist01;
  }

  public void setDist01(String dist01) {
    checkWritable();
    this.dist01 = dist01;
  }

  public String getDist02() {
    return dist02;
  }

  public void setDist02(String dist02) {
    checkWritable();
    this.dist02 = dist02;
  }

  public String getDist03() {
    return dist03;
  }

  public void setDist03(String dist03) {
    checkWritable();
    this.dist03 = dist03;
  }

  public String getDist04() {
    return dist04;
  }

  public void setDist04(String dist04) {
    checkWritable();
    this.dist04 = dist04;
  }

  public String getDist05() {
    return dist05;
  }

  public void setDist05(String dist05) {
    checkWritable();
    this.dist05 = dist05;
  }

  public String getDist06() {
    return dist06;
  }

  public void setDist06(String dist06) {
    checkWritable();
    this.dist06 = dist06;
  }

  public String getDist07() {
    return dist07;
  }

  public void setDist07(String dist07) {
    checkWritable();
    this.dist07 = dist07;
  }

  public String getDist08() {
    return dist08;
  }

  public void setDist08(String dist08) {
    checkWritable();
    this.dist08 = dist08;
  }

  public String getDist09() {
    return dist09;
  }

  public void setDist09(String dist09) {
    checkWritable();
    this.dist09 = dist09;
  }

  public String getDist10() {
    return dist10;
  }

  public void setDist10(String dist10) {
    checkWritable();
    this.dist10 = dist10;
  }

  @Override
  public StockData clone() {
    return (StockData) super.clone();
  }
}
