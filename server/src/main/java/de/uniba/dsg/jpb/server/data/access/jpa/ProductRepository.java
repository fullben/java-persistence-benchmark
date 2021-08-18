package de.uniba.dsg.jpb.server.data.access.jpa;

import de.uniba.dsg.jpb.server.data.model.jpa.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {}
