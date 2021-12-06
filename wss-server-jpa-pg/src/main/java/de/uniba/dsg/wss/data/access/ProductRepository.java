package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for accessing {@link ProductEntity products}.
 *
 * @author Benedikt Full
 */
public interface ProductRepository extends JpaRepository<ProductEntity, String> {}
