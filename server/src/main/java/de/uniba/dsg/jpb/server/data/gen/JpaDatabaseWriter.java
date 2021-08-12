package de.uniba.dsg.jpb.server.data.gen;

import de.uniba.dsg.jpb.server.data.model.jpa.WarehouseEntity;
import de.uniba.dsg.jpb.server.data.access.jpa.CarrierRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.ProductRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.WarehouseRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaDatabaseWriter {

  private final ProductRepository productRepository;
  private final CarrierRepository carrierRepository;
  private final WarehouseRepository warehouseRepository;

  @Autowired
  public JpaDatabaseWriter(
      ProductRepository productRepository,
      CarrierRepository carrierRepository,
      WarehouseRepository warehouseRepository) {
    this.productRepository = productRepository;
    this.carrierRepository = carrierRepository;
    this.warehouseRepository = warehouseRepository;
  }

  public List<WarehouseEntity> writeAll(JpaDataGenerator generator) {
    productRepository.saveAll(generator.getProducts());
    carrierRepository.saveAll(generator.getCarriers());
    return toList(warehouseRepository.saveAll(generator.getWarehouses()));
  }

  private static <T> List<T> toList(Iterable<T> iterable) {
    List<T> list = new ArrayList<>();
    for (T i : iterable) {
      list.add(i);
    }
    return list;
  }
}
