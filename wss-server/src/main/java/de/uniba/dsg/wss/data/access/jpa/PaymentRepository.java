package de.uniba.dsg.wss.data.access.jpa;

import de.uniba.dsg.wss.data.model.jpa.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for accessing and modifying customer {@link PaymentEntity payments}.
 *
 * @author Benedikt Full
 */
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {}
