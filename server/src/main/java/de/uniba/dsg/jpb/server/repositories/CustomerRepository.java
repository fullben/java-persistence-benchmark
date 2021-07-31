package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.server.model.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {}
