package de.uniba.dsg.wss.ms.data.model;

/**
 * Base class for all data classes that represent an actual human person.
 *
 * @author Benedikt Full
 */
public abstract class PersonData extends BaseData {

  private String firstName;
  private String middleName;
  private String lastName;
  private AddressData address;
  private String phoneNumber;
  private String email;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    checkWritable();
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    checkWritable();
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    checkWritable();
    this.lastName = lastName;
  }

  public AddressData getAddress() {
    return address;
  }

  public void setAddress(AddressData address) {
    checkWritable();
    this.address = address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    checkWritable();
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    checkWritable();
    this.email = email;
  }
}
