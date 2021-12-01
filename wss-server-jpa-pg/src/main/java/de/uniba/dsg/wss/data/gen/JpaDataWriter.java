package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.access.CarrierRepository;
import de.uniba.dsg.wss.data.access.EmployeeRepository;
import de.uniba.dsg.wss.data.access.ProductRepository;
import de.uniba.dsg.wss.data.access.WarehouseRepository;
import de.uniba.dsg.wss.data.model.CarrierEntity;
import de.uniba.dsg.wss.data.model.EmployeeEntity;
import de.uniba.dsg.wss.data.model.ProductEntity;
import de.uniba.dsg.wss.data.model.WarehouseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Can be used to write a wholesale supplier data model to a JPA-based persistence solution.
 *
 * @author Benedikt Full
 */
@Component
public class JpaDataWriter
    implements DataWriter<ProductEntity, WarehouseEntity, EmployeeEntity, CarrierEntity> {

  private static final Logger LOG = LogManager.getLogger(JpaDataWriter.class);
  private final ProductRepository productRepository;
  private final CarrierRepository carrierRepository;
  private final WarehouseRepository warehouseRepository;
  private final EmployeeRepository employeeRepository;

  @Autowired
  public JpaDataWriter(
      ProductRepository productRepository,
      CarrierRepository carrierRepository,
      WarehouseRepository warehouseRepository,
      EmployeeRepository employeeRepository) {
    this.productRepository = productRepository;
    this.carrierRepository = carrierRepository;
    this.warehouseRepository = warehouseRepository;
    this.employeeRepository = employeeRepository;
  }

  @Override
  public void write(
      DataModel<ProductEntity, WarehouseEntity, EmployeeEntity, CarrierEntity> model) {
    if (!supports(model)) {
      throw new UnsupportedDataModelException("Data model was null");
    }
    Stopwatch stopwatch = new Stopwatch().start();
    productRepository.saveAll(model.getProducts());
    carrierRepository.saveAll(model.getCarriers());
    warehouseRepository.saveAll(model.getWarehouses());
    employeeRepository.saveAll(model.getEmployees());
    stopwatch.stop();
    LOG.info("Wrote model data to database, took {}", stopwatch.getDuration());
  }
}
