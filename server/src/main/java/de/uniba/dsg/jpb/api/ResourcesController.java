package de.uniba.dsg.jpb.api;

import de.uniba.dsg.jpb.data.transfer.representations.CarrierRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.CustomerRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.DistrictRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.EmployeeRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.OrderRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.ProductRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.StockRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.WarehouseRepresentation;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface ResourcesController {

  Iterable<ProductRepresentation> getProducts();

  ResponseEntity<EmployeeRepresentation> getEmployee(String username);

  List<WarehouseRepresentation> getWarehouses();

  ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId);

  ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId);

  ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      String warehouseId, String districtId);

  ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      String warehouseId, String districtId);

  List<CarrierRepresentation> getCarriers();
}
