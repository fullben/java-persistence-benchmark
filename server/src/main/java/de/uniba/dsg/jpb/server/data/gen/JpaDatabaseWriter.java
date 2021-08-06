package de.uniba.dsg.jpb.server.data.gen;

import de.uniba.dsg.jpb.server.data.access.jpa.ProductRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JpaDatabaseWriter {

  private final ProductRepository productRepository;
  private final WarehouseRepository warehouseRepository;

  @Autowired
  public JpaDatabaseWriter(
      ProductRepository productRepository, WarehouseRepository warehouseRepository) {
    this.productRepository = productRepository;
    this.warehouseRepository = warehouseRepository;
  }

  public void writeAll(JpaDataGenerator generator) {
    productRepository.saveAll(generator.getProducts());
    warehouseRepository.saveAll(generator.getWarehouses());
  }
}
