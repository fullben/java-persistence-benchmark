package de.uniba.dsg.wss.data.access.jpa;

import de.uniba.dsg.wss.data.model.jpa.CarrierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for accessing and modifying {@link CarrierEntity carriers}.
 *
 * @author Benedikt Full
 */
public interface CarrierRepository extends JpaRepository<CarrierEntity, String> {}
