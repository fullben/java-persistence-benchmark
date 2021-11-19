package de.uniba.dsg.wss.ms.data.model;

import org.jacis.plugin.objectadapter.cloning.JacisCloneable;

/**
 * A carrier is responsible for fulfilling {@link OrderData orders} by delivering the ordered items
 * to the {@link CustomerData customer}.
 *
 * @author Benedikt Full
 */
public class CarrierData extends BaseData implements JacisCloneable<CarrierData> {

  private String name;
  private String phoneNumber;
  private AddressData address;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    checkWritable();
    this.name = name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    checkWritable();
    this.phoneNumber = phoneNumber;
  }

  public AddressData getAddress() {
    return address;
  }

  public void setAddress(AddressData address) {
    checkWritable();
    this.address = address;
  }

  @Override
  public CarrierData clone() {
    return (CarrierData) super.clone();
  }
}
