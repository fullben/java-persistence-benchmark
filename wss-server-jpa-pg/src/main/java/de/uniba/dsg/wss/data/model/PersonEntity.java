package de.uniba.dsg.wss.data.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

/**
 * Base class for all entities that represent an actual human person.
 *
 * @author Benedikt Full
 */
@MappedSuperclass
public abstract class PersonEntity extends BaseEntity {

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String middleName;

  @Column(nullable = false)
  private String lastName;

  @Embedded private AddressEmbeddable address;

  @Column(nullable = false)
  private String phoneNumber;

  @Column(nullable = false, unique = true)
  private String email;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public AddressEmbeddable getAddress() {
    return address;
  }

  public void setAddress(AddressEmbeddable address) {
    this.address = address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
