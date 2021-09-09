package de.uniba.dsg.wss.data.access.jpa;

import de.uniba.dsg.wss.data.model.jpa.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for accessing and modifying {@link WarehouseEntity warehouses}.
 *
 * @author Benedikt Full
 */
@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, String> {}
