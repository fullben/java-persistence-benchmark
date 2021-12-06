package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CustomerEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA repository for accessing and modifying {@link CustomerEntity customers}.
 *
 * @author Benedikt Full
 */
@Transactional(readOnly = true)
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {

  List<CustomerEntity> findByDistrictId(String districtId);

  Optional<CustomerEntity> findByEmail(String email);
}
