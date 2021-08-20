package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.data.model.ms.CustomerData;

public class CustomerRepository extends IndexedIdRepository<CustomerData, Long> {

  CustomerRepository() {
    super();
  }
}
