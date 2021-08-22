package de.uniba.dsg.jpb.data.model.ms;

import de.uniba.dsg.jpb.util.Identifiable;

public class CarrierData implements Identifiable<Long> {

  private Long id;
  private String name;
  private String phoneNumber;
  private AddressData address;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

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
