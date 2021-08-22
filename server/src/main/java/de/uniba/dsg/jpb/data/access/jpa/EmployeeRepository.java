package de.uniba.dsg.jpb.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.EmployeeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

  Optional<EmployeeEntity> findByUsername(String username);
}
