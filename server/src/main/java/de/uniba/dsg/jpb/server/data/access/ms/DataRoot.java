package de.uniba.dsg.jpb.server.data.access.ms;

public class DataRoot {

  private WarehouseRepository warehouseRepository;
  private ProductRepository productRepository;

  public DataRoot() {
    warehouseRepository = new WarehouseRepository();
    productRepository = new ProductRepository();
  }
}
