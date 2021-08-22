package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.util.IdentifierGenerator;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

public class DataRoot {

  private final ProductRepository productRepository;
  private final CarrierRepository carrierRepository;
  private final WarehouseRepository warehouseRepository;
  private final EmployeeRepository employeeRepository;

  public DataRoot() {
    productRepository = new ProductRepository();
    carrierRepository = new CarrierRepository();
    warehouseRepository = new WarehouseRepository();
    employeeRepository = new EmployeeRepository();
  }

  public ProductRepository productRepository() {
    return productRepository;
  }

  public CarrierRepository carrierRepository() {
    return carrierRepository;
  }

  public WarehouseRepository warehouseRepository() {
    return warehouseRepository;
  }

  public EmployeeRepository employeeRepository() {
    return employeeRepository;
  }

  public void setStorageManager(EmbeddedStorageManager storageManager) {
    productRepository.setStorageManager(storageManager);
    carrierRepository.setStorageManager(storageManager);
    warehouseRepository.setStorageManager(storageManager);
    employeeRepository.setStorageManager(storageManager);
  }

  public void setIdGenerator(IdentifierGenerator<Long> idGenerator) {
    productRepository.setIdGenerator(idGenerator);
    carrierRepository.setIdGenerator(idGenerator);
    warehouseRepository.setIdGenerator(idGenerator);
    employeeRepository.setIdGenerator(idGenerator);
  }
}
