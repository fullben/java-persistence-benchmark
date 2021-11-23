package de.uniba.dsg.wss.data.model.ms;

/**
 * Base class for all data classes that represent an actual human person.
 *
 * @author Benedikt Full
 */
public abstract class PersonData extends BaseData {

  private final String firstName;
  private final String middleName;
  private final String lastName;
  private final AddressData address;
  private final String phoneNumber;
  private final String email;

  public PersonData(String id,
                    String firstName,
                    String middleName,
                    String lastName,
                    AddressData address,
                    String phoneNumber,
                    String email) {
    super(id);
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public AddressData getAddress() {
    return address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getEmail() {
    return email;
  }
}
