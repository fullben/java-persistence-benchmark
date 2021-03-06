package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for accessing and modifying {@link WarehouseEntity warehouses}.
 *
 * @author Benedikt Full
 */
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, String> {}
