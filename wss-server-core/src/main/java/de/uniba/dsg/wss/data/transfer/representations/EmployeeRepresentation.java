package de.uniba.dsg.wss.data.transfer.representations;

public class EmployeeRepresentation {

  private String id;
  private String firstName;
  private String middleName;
  private String lastName;
  private AddressRepresentation address;
  private String phoneNumber;
  private String email;
  private DistrictRepresentation district;
  private String title;
  private String username;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public AddressRepresentation getAddress() {
    return address;
  }

  public void setAddress(AddressRepresentation address) {
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

  public DistrictRepresentation getDistrict() {
    return district;
  }

  public void setDistrict(DistrictRepresentation district) {
    this.district = district;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
