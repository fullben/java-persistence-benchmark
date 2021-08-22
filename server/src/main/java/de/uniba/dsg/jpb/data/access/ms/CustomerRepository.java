package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.data.model.ms.CustomerData;

public class CustomerRepository extends IndexedIdRepository<CustomerData, Long> {

  CustomerRepository() {
    super();
  }

  public CustomerData getByEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException();
    }
    return read(
        () ->
            requireFound(
                findAll().stream().filter(c -> c.getEmail().equals(email)).findAny().orElse(null)));
  }
}
