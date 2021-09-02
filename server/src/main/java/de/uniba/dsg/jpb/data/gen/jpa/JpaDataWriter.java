package de.uniba.dsg.jpb.data.gen.jpa;

import de.uniba.dsg.jpb.data.access.jpa.CarrierRepository;
import de.uniba.dsg.jpb.data.access.jpa.EmployeeRepository;
import de.uniba.dsg.jpb.data.access.jpa.ProductRepository;
import de.uniba.dsg.jpb.data.access.jpa.WarehouseRepository;
import de.uniba.dsg.jpb.data.gen.DataWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Can be used to write a wholesale supplier data model to a JPA-based persistence solution.
 *
 * @author Benedikt Full
 */
@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaDataWriter implements DataWriter<JpaDataGenerator> {

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
  public void writeAll(JpaDataGenerator generator) {
    productRepository.saveAll(generator.getProducts());
    carrierRepository.saveAll(generator.getCarriers());
    warehouseRepository.saveAll(generator.getWarehouses());
    employeeRepository.saveAll(generator.getEmployees());
  }
}
