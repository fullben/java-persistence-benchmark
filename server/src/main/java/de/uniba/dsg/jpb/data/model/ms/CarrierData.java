package de.uniba.dsg.jpb.data.model.ms;

/**
 * A carrier is responsible for fulfilling {@link OrderData Orders} by delivering the ordered items
 * to the {@link CustomerData Customer}.
 *
 * @author Benedikt Full
 */
public class CarrierData extends BaseData {

  private String name;
  private String phoneNumber;
  private AddressData address;

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

  public AddressData getAddress() {
    return address;
  }

  public void setAddress(AddressData address) {
    this.address = address;
  }
}
