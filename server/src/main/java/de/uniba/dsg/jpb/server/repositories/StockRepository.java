package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.server.model.Stock;
import de.uniba.dsg.jpb.server.model.Warehouse;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface StockRepository extends CrudRepository<Stock, Long> {
  Optional<Stock> findByItemId(Long id);

  Optional<Stock> findByItemIdAndWarehouseId(Long itemId, Warehouse warehouseId);
}
