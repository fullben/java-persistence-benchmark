package de.uniba.dsg.wss.data.access.jpa;

import de.uniba.dsg.wss.data.model.jpa.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for accessing and modifying {@link WarehouseEntity warehouses}.
 *
 * @author Benedikt Full
 */
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, String> {}
