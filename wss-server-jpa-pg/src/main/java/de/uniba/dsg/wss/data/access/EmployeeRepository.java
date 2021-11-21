package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.EmployeeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA repository for accessing and modifying {@link EmployeeEntity employees}.
 *
 * @author Benedikt Full
 */
@Transactional(readOnly = true)
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {

  Optional<EmployeeEntity> findByUsername(String username);
}
