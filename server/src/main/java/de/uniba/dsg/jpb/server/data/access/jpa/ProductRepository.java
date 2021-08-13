package de.uniba.dsg.jpb.server.data.access.jpa;

import de.uniba.dsg.jpb.server.data.model.jpa.ProductEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, Long> {

  @Query(value = "SELECT id FROM products", nativeQuery = true)
  List<Long> findAllProductIds();
}
