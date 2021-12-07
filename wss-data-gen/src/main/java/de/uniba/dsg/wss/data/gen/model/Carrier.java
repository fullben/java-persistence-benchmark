package de.uniba.dsg.wss.data.gen.model;

/**
 * A carrier is responsible for fulfilling {@link Order orders} by delivering the ordered items to
 * the {@link Customer customer}.
 *
 * @author Benedikt Full
 */
public class Carrier extends Base {

  private String name;
  private String phoneNumber;
  private Address address;

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

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
}
