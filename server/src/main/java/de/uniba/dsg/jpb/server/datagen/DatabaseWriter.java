package de.uniba.dsg.jpb.server.datagen;

import de.uniba.dsg.jpb.server.repositories.ItemRepository;
import de.uniba.dsg.jpb.server.repositories.NewOrderRepository;
import de.uniba.dsg.jpb.server.repositories.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseWriter {

  private final ItemRepository itemRepository;
  private final WarehouseRepository warehouseRepository;
  private final NewOrderRepository newOrderRepository;

  @Autowired
  public DatabaseWriter(
      ItemRepository itemRepository,
      WarehouseRepository warehouseRepository,
      NewOrderRepository newOrderRepository) {
    this.itemRepository = itemRepository;
    this.warehouseRepository = warehouseRepository;
    this.newOrderRepository = newOrderRepository;
  }

  public void writeItems(TpccDataGenerator generator) {
    itemRepository.saveAll(generator.getItems());
  }

  public void writeAll(TpccDataGenerator generator) {
    itemRepository.saveAll(generator.getItems());
    warehouseRepository.saveAll(generator.getWarehouses());
    newOrderRepository.saveAll(generator.getNewOrders());
  }

  public void writeAll(FakerDataGenerator generator) {
    itemRepository.saveAll(generator.getItems());
    warehouseRepository.saveAll(generator.getWarehouses());
    newOrderRepository.saveAll(generator.getNewOrders());
  }
}
