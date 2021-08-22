package de.uniba.dsg.jpb.data.gen.jpa;

import de.uniba.dsg.jpb.data.access.jpa.CarrierRepository;
import de.uniba.dsg.jpb.data.access.jpa.EmployeeRepository;
import de.uniba.dsg.jpb.data.access.jpa.ProductRepository;
import de.uniba.dsg.jpb.data.access.jpa.WarehouseRepository;
import de.uniba.dsg.jpb.data.gen.DataProvider;
import de.uniba.dsg.jpb.data.gen.DataWriter;
import de.uniba.dsg.jpb.data.model.jpa.CarrierEntity;
import de.uniba.dsg.jpb.data.model.jpa.EmployeeEntity;
import de.uniba.dsg.jpb.data.model.jpa.ProductEntity;
import de.uniba.dsg.jpb.data.model.jpa.WarehouseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaDataWriter
    implements DataWriter<WarehouseEntity, EmployeeEntity, ProductEntity, CarrierEntity> {

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
  public void writeAll(
      DataProvider<WarehouseEntity, EmployeeEntity, ProductEntity, CarrierEntity> dataProvider) {
    productRepository.saveAll(dataProvider.getProducts());
    carrierRepository.saveAll(dataProvider.getCarriers());
    warehouseRepository.saveAll(dataProvider.getWarehouses());
    employeeRepository.saveAll(dataProvider.getEmployees());
  }
}
