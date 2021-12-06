package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for accessing and modifying customer {@link PaymentEntity payments}.
 *
 * @author Benedikt Full
 */
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {}
