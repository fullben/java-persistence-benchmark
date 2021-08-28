package de.uniba.dsg.jpb.data.model.jpa;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The available amount of a specific {@link ProductEntity Product} at some {@link WarehouseEntity
 * Warehouse}.
 *
 * @author Benedikt Full
 */
@Entity
@Table(name = "stocks")
public class StockEntity extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id")
  private ProductEntity product;

  private int quantity;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "warehouse_id")
  private WarehouseEntity warehouse;

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

  public ProductEntity getProduct() {
    return product;
  }

  public void setProduct(ProductEntity product) {
    this.product = product;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public WarehouseEntity getWarehouse() {
    return warehouse;
  }

  public void setWarehouse(WarehouseEntity warehouse) {
    this.warehouse = warehouse;
  }

  public double getYearToDateBalance() {
    return yearToDateBalance;
  }

  public void setYearToDateBalance(double yearToDateBalance) {
    this.yearToDateBalance = yearToDateBalance;
  }

  public int getOrderCount() {
    return orderCount;
  }

  public void setOrderCount(int orderCount) {
    this.orderCount = orderCount;
  }

  public int getRemoteCount() {
    return remoteCount;
  }

  public void setRemoteCount(int remoteCount) {
    this.remoteCount = remoteCount;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getDist01() {
    return dist01;
  }

  public void setDist01(String dist01) {
    this.dist01 = dist01;
  }

  public String getDist02() {
    return dist02;
  }

  public void setDist02(String dist02) {
    this.dist02 = dist02;
  }

  public String getDist03() {
    return dist03;
  }

  public void setDist03(String dist03) {
    this.dist03 = dist03;
  }

  public String getDist04() {
    return dist04;
  }

  public void setDist04(String dist04) {
    this.dist04 = dist04;
  }

  public String getDist05() {
    return dist05;
  }

  public void setDist05(String dist05) {
    this.dist05 = dist05;
  }

  public String getDist06() {
    return dist06;
  }

  public void setDist06(String dist06) {
    this.dist06 = dist06;
  }

  public String getDist07() {
    return dist07;
  }

  public void setDist07(String dist07) {
    this.dist07 = dist07;
  }

  public String getDist08() {
    return dist08;
  }

  public void setDist08(String dist08) {
    this.dist08 = dist08;
  }

  public String getDist09() {
    return dist09;
  }

  public void setDist09(String dist09) {
    this.dist09 = dist09;
  }

  public String getDist10() {
    return dist10;
  }

  public void setDist10(String dist10) {
    this.dist10 = dist10;
  }
}
