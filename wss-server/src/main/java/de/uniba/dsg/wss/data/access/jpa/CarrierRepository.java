package de.uniba.dsg.wss.data.access.jpa;

import de.uniba.dsg.wss.data.model.jpa.CarrierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for accessing and modifying {@link CarrierEntity carriers}.
 *
 * @author Benedikt Full
 */
@Repository
public interface CarrierRepository extends JpaRepository<CarrierEntity, String> {}
