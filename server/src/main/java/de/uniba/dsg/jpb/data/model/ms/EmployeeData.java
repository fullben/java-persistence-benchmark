package de.uniba.dsg.jpb.data.model.ms;

import org.jacis.plugin.objectadapter.cloning.JacisCloneable;

/**
 * An employee of the wholesale supplier. Employees are the user group meant to perform the business
 * transactions, i.e. create new orders, or add new payments.
 *
 * @author Benedikt Full
 */
public class EmployeeData extends PersonData implements JacisCloneable<EmployeeData> {

  private String title;
  private String username;
  private String password;
  private String districtId;
  private String districtWarehouseId;

  public EmployeeData() {}

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    checkWritable();
    this.title = title;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    checkWritable();
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    checkWritable();
    this.password = password;
  }

  public String getDistrictId() {
    return districtId;
  }

  public void setDistrictId(String districtId) {
    checkWritable();
    this.districtId = districtId;
  }

  public String getDistrictWarehouseId() {
    return districtWarehouseId;
  }

  public void setDistrictWarehouseId(String districtWarehouseId) {
    this.districtWarehouseId = districtWarehouseId;
  }

  @Override
  public EmployeeData clone() {
    return (EmployeeData) super.clone();
  }
}
