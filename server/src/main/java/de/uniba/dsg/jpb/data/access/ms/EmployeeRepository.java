package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
import java.util.Optional;

public class EmployeeRepository extends IndexedIdRepository<EmployeeData, Long> {

  EmployeeRepository() {
    super();
  }

  public Optional<EmployeeData> findByUsername(String username) {
    return read(
        () ->
            getIndexedItems().values().stream()
                .filter(e -> e.getUsername().equals(username))
                .findAny());
  }
}
