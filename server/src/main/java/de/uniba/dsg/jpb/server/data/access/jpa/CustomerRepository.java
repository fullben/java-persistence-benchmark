package de.uniba.dsg.jpb.server.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.CustomerEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

  List<CustomerEntity> findByDistrictId(Long districtId);
}
