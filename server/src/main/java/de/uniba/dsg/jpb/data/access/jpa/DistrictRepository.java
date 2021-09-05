package de.uniba.dsg.jpb.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.DistrictEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for accessing and modifying {@link DistrictEntity districts}.
 *
 * @author Benedikt Full
 */
@Repository
public interface DistrictRepository extends JpaRepository<DistrictEntity, String> {

  List<DistrictEntity> findByWarehouseId(String warehouseId);
}
