package de.uniba.dsg.jpb.server.data.access.jpa;

import de.uniba.dsg.jpb.server.data.model.jpa.CustomerEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

  List<CustomerEntity> findByDistrictId(Long districtId);

  Optional<CustomerEntity> findByEmail(String email);
}
