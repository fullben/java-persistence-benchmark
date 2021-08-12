package de.uniba.dsg.jpb.server.data.access.jpa;

import de.uniba.dsg.jpb.server.data.model.jpa.DistrictEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<DistrictEntity, Long> {

  List<DistrictEntity> findByWarehouseId(Long warehouseId);
}
