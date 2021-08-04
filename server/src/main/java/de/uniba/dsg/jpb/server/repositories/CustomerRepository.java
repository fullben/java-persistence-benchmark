package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.server.model.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

  List<Customer> findByDistrictId(Long districtId);
}
