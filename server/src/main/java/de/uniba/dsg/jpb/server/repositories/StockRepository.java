package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.model.Stock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

  Optional<Stock> findByItemIdAndWarehouseId(Long itemId, Long warehouseId);

  List<Stock> findByWarehouseId(Long warehouseId);
}
