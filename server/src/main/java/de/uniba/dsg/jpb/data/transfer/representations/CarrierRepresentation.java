package de.uniba.dsg.jpb.data.transfer.representations;

public class CarrierRepresentation {

  private Long id;
  private String name;
  private String phoneNumber;
  private AddressRepresentation address;

  public Long getId() {
    return id;
  }

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

  public AddressRepresentation getAddress() {
    return address;
  }

  public void setAddress(AddressRepresentation address) {
    this.address = address;
  }
}
