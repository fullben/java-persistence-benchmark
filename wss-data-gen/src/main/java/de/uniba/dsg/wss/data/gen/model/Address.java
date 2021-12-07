package de.uniba.dsg.wss.data.gen.model;

/**
 * A representation of a United States address.
 *
 * @author Benedikt Full
 */
public class Address {

  private String street1;
  private String street2;
  private String zipCode;
  private String city;
  private String state;

  public Address() {}

  public Address(Address other) {
    this.street1 = other.street1;
    this.street2 = other.street2;
    this.zipCode = other.zipCode;
    this.city = other.city;
    this.state = other.state;
  }

  public String getStreet1() {
    return street1;
  }

  public void setStreet1(String street1) {
    this.street1 = street1;
  }

  public String getStreet2() {
    return street2;
  }

  public void setStreet2(String street2) {
    this.street2 = street2;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }
}
