package de.uniba.dsg.jpb.data.access.ms;

import one.microstream.storage.embedded.types.EmbeddedStorageManager;

public class DataRoot {

  private final WarehouseRepository warehouseRepository;
  private final ProductRepository productRepository;

  public DataRoot() {
    warehouseRepository = new WarehouseRepository();
    productRepository = new ProductRepository();
  }

  public WarehouseRepository getWarehouseRepository() {
    return warehouseRepository;
  }

  public ProductRepository getProductRepository() {
    return productRepository;
  }

  public void setStorageManager(EmbeddedStorageManager storageManager) {
    warehouseRepository.setStorageManager(storageManager);
    productRepository.setStorageManager(storageManager);
  }
}
