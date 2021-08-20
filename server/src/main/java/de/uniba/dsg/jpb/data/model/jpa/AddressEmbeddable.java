package de.uniba.dsg.jpb.data.model.jpa;

import javax.persistence.Embeddable;

@Embeddable
public class AddressEmbeddable {

  private String street1;
  private String street2;
  private String zipCode;
  private String city;
  private String state;

  public AddressEmbeddable() {
    street1 = null;
    street2 = null;
    zipCode = null;
    city = null;
    state = null;
  }

  public AddressEmbeddable(AddressEmbeddable address) {
    street1 = address.street1;
    street2 = address.street2;
    zipCode = address.zipCode;
    city = address.city;
    state = address.state;
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
