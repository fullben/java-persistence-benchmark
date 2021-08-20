package de.uniba.dsg.jpb.server.data.model.ms;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EmployeeData extends PersonData {

  private DistrictData district;
  private String title;
  private String username;
  @JsonIgnore private String password;

  public DistrictData getDistrict() {
    return district;
  }

  public void setDistrict(DistrictData district) {
    this.district = district;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
