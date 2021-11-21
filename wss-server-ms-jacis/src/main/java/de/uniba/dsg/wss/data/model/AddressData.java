package de.uniba.dsg.wss.data.model;

/**
 * An immutable representation of a United States address.
 *
 * @author Benedikt Full
 */
public class AddressData {

  private final String street1;
  private final String street2;
  private final String zipCode;
  private final String city;
  private final String state;

  public AddressData(String street1, String street2, String zipCode, String city, String state) {
    this.street1 = street1;
    this.street2 = street2;
    this.zipCode = zipCode;
    this.city = city;
    this.state = state;
  }

  public String getStreet1() {
    return street1;
  }

  public String getStreet2() {
    return street2;
  }

  public String getZipCode() {
    return zipCode;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }
}
