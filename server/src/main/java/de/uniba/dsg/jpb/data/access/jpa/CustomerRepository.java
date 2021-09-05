package de.uniba.dsg.jpb.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.CustomerEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for accessing and modifying {@link CustomerEntity customers}.
 *
 * @author Benedikt Full
 */
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {

  List<CustomerEntity> findByDistrictId(String districtId);

  Optional<CustomerEntity> findByEmail(String email);
}
