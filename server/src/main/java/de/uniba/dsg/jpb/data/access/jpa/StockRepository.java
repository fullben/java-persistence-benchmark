package de.uniba.dsg.jpb.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.StockEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

  Optional<StockEntity> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

  @Query(
      value =
          "SELECT * FROM stocks s WHERE s.warehouse_id = :warehouseId AND s.product_id IN :productIds AND s.quantity < :quantityThreshold",
      nativeQuery = true)
  List<StockEntity> findByWarehouseIdAndProductIdAndQuantityThreshold(
      Long warehouseId, Collection<Long> productIds, int quantityThreshold);

  List<StockEntity> findByWarehouseId(Long warehouseId);
}
