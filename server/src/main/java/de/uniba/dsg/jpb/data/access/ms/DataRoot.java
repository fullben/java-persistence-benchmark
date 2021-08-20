package de.uniba.dsg.jpb.data.access.ms;

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

  public ProductRepository getProductRepository() {
    return productRepository;
  }

  public CarrierRepository getCarrierRepository() {
    return carrierRepository;
  }

  public WarehouseRepository getWarehouseRepository() {
    return warehouseRepository;
  }

  public EmployeeRepository getEmployeeRepository() {
    return employeeRepository;
  }

  public void setStorageManager(EmbeddedStorageManager storageManager) {
    productRepository.setStorageManager(storageManager);
    carrierRepository.setStorageManager(storageManager);
    warehouseRepository.setStorageManager(storageManager);
    employeeRepository.setStorageManager(storageManager);
  }
}
