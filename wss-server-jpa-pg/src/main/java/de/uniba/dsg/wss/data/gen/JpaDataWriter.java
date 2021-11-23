package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.access.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Can be used to write a wholesale supplier data model to a JPA-based persistence solution.
 *
 * @author Benedikt Full
 */
@Component
public class JpaDataWriter implements DataWriter<JpaDataConverter> {

  private static final Logger LOG = LogManager.getLogger(JpaDataWriter.class);
  private final ProductRepository productRepository;
  private final CarrierRepository carrierRepository;
  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final EmployeeRepository employeeRepository;

  @Autowired
  public JpaDataWriter(
      ProductRepository productRepository,
      CarrierRepository carrierRepository,
      WarehouseRepository warehouseRepository,
      DistrictRepository districtRepository,
      EmployeeRepository employeeRepository) {
    this.productRepository = productRepository;
    this.carrierRepository = carrierRepository;
    this.warehouseRepository = warehouseRepository;
    this.districtRepository = districtRepository;
    this.employeeRepository = employeeRepository;
  }

  @Transactional
  @Override
  public void writeAll(JpaDataConverter generator) {
    Stopwatch stopwatch = new Stopwatch(true);
    productRepository.saveAll(generator.getProducts());
    carrierRepository.saveAll(generator.getCarriers());
    warehouseRepository.saveAll(generator.getWarehouses());
    employeeRepository.saveAll(generator.getEmployees());
    stopwatch.stop();
    LOG.info("Wrote model data to database, took {}", stopwatch.getDuration());
  }
}
