package de.uniba.dsg.jpb.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for accessing {@link ProductEntity products}.
 *
 * @author Benedikt Full
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {}
