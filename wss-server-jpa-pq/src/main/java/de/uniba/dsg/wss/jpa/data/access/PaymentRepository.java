package de.uniba.dsg.wss.jpa.data.access;

import de.uniba.dsg.wss.jpa.data.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for accessing and modifying customer {@link PaymentEntity payments}.
 *
 * @author Benedikt Full
 */
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {}
