package de.uniba.dsg.jpb.server.data.access.ms;

import de.uniba.dsg.jpb.server.data.model.ms.CustomerData;

public class CustomerRepository extends IndexedIdRepository<CustomerData, Long> {

  CustomerRepository() {
    super();
  }
}
