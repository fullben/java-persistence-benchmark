package de.uniba.dsg.jpb.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.CarrierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for accessing and modifying {@link CarrierEntity carriers}.
 *
 * @author Benedikt Full
 */
@Repository
public interface CarrierRepository extends JpaRepository<CarrierEntity, String> {}
