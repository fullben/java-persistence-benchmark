package de.uniba.dsg.wss.data.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * An employee of the wholesale supplier. Employees are the user group meant to perform the business
 * transactions, i.e. create new orders, or add new payments.
 *
 * @author Benedikt Full
 */
@Entity(name = "Employee")
@Table(
    name = "employees",
    indexes = {@Index(name = "employees_idx_username", columnList = "username")})
public class EmployeeEntity extends PersonEntity {

  @OneToOne(optional = false, fetch = FetchType.EAGER)
  private DistrictEntity district;

  @Column(nullable = false)
  private String title;

  @Column(unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  public DistrictEntity getDistrict() {
    return district;
  }

  public void setDistrict(DistrictEntity district) {
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
