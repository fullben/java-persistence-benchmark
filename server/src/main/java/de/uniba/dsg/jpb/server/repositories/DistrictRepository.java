package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.server.model.District;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<District, Long> {

  List<District> findByWarehouseId(Long warehouseId);
}
