package de.uniba.dsg.wss.data.gen;

/**
 * Data regarding the model objects that can be created by some specific instance of the {@link
 * DataGenerator}.
 *
 * @author Benedikt Full
 */
public class Configuration {

  private int productCount;
  private int warehouseCount;
  private int districtCount;
  private int employeeCount;
  private int customerCount;
  private int orderCount;

  public Configuration() {
    productCount = 0;
    warehouseCount = 0;
    districtCount = 0;
    employeeCount = 0;
    customerCount = 0;
    orderCount = 0;
  }

  public int getProductCount() {
    return productCount;
  }

  public void setProductCount(int productCount) {
    this.productCount = productCount;
  }

  public int getWarehouseCount() {
    return warehouseCount;
  }

  public void setWarehouseCount(int warehouseCount) {
    this.warehouseCount = warehouseCount;
  }

  public int getDistrictCount() {
    return districtCount;
  }

  public void setDistrictCount(int districtCount) {
    this.districtCount = districtCount;
  }

  public int getEmployeeCount() {
    return employeeCount;
  }

  public void setEmployeeCount(int employeeCount) {
    this.employeeCount = employeeCount;
  }

  public int getCustomerCount() {
    return customerCount;
  }

  public void setCustomerCount(int customerCount) {
    this.customerCount = customerCount;
  }

  public int getOrderCount() {
    return orderCount;
  }

  public void setOrderCount(int orderCount) {
    this.orderCount = orderCount;
  }
}
