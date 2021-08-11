package de.uniba.dsg.jpb.data.model.ms;

public class CarrierData {

  private Long id;
  private String name;
  private AddressData address;

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

  public AddressData getAddress() {
    return address;
  }

  public void setAddress(AddressData address) {
    this.address = address;
  }
}
