package de.uniba.dsg.jpb.server.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.StockEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

  Optional<StockEntity> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

  List<StockEntity> findByWarehouseId(Long warehouseId);
}
