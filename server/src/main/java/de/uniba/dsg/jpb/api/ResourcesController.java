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

  EmployeeRepresentation getEmployee(String username);

  List<WarehouseRepresentation> getWarehouses();

  List<DistrictRepresentation> getWarehouseDistricts(Long warehouseId);

  List<StockRepresentation> getWarehouseStocks(Long warehouseId);

  ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(Long warehouseId, Long districtId);

  ResponseEntity<List<OrderRepresentation>> getDistrictOrders(Long warehouseId, Long districtId);

  List<CarrierRepresentation> getCarriers();
}
