package de.uniba.dsg.jpb.data.model.jpa;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A carrier is responsible for fulfilling {@link OrderEntity Orders} by delivering the ordered
 * items to the {@link CustomerEntity Customer}.
 *
 * @author Benedikt Full
 */
@Entity
@Table(name = "carriers")
public class CarrierEntity extends BaseEntity {

  @Column(unique = true)
  private String name;

  private String phoneNumber;

  @Embedded private AddressEmbeddable address;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public AddressEmbeddable getAddress() {
    return address;
  }

  public void setAddress(AddressEmbeddable address) {
    this.address = address;
  }
}
