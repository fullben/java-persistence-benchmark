package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.data.model.CarrierEntity;
import de.uniba.dsg.wss.data.model.EmployeeEntity;
import de.uniba.dsg.wss.data.model.ProductEntity;
import de.uniba.dsg.wss.data.model.WarehouseEntity;
import java.util.List;

/**
 * Class for storing the converted model data produced by {@link JpaDataConverter} instances.
 *
 * @author Benedikt Full
 */
public class JpaDataModel
    extends BaseDataModel<ProductEntity, WarehouseEntity, EmployeeEntity, CarrierEntity> {

  public JpaDataModel(
      List<ProductEntity> products,
      List<WarehouseEntity> warehouses,
      List<EmployeeEntity> employees,
      List<CarrierEntity> carriers,
      Stats stats) {
    super(products, warehouses, employees, carriers, stats);
  }
}
