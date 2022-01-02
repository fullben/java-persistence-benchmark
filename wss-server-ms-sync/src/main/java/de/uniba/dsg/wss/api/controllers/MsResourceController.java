package de.uniba.dsg.wss.api.controllers;

import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.EmployeeData;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.data.transfer.representations.CarrierRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.CustomerRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.DistrictRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.EmployeeRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.OrderRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.ProductRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.StockRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.WarehouseRepresentation;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides read-only access to many of the resources managed by the server.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 */
@RestController
public class MsResourceController implements ResourceController {

  private final MsDataRoot dataRoot;
  private final ModelMapper modelMapper;

  @Autowired
  public MsResourceController(MsDataRoot dataRoot) {
    this.dataRoot = dataRoot;
    modelMapper = new ModelMapper();
  }

  @Override
  public ResponseEntity<Iterable<ProductRepresentation>> getProducts() {
    return ResponseEntity.ok(
        dataRoot.getProducts().entrySet().stream()
            .parallel()
            .map(p -> modelMapper.map(p.getValue(), ProductRepresentation.class))
            .collect(Collectors.toList()));
  }

  @Override
  public ResponseEntity<EmployeeRepresentation> getEmployee(String username) {
    EmployeeData employee = dataRoot.getEmployees().get(username);
    if (employee == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(modelMapper.map(employee, EmployeeRepresentation.class));
  }

  @Override
  public ResponseEntity<List<WarehouseRepresentation>> getWarehouses() {
    return ResponseEntity.ok(
        dataRoot.getWarehouses().values().stream()
            .map(warehouseData -> modelMapper.map(warehouseData, WarehouseRepresentation.class))
            .collect(Collectors.toList()));
  }

  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId) {
    WarehouseData warehouse = dataRoot.getWarehouses().get(warehouseId);
    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    List<DistrictRepresentation> districtRepresentations =
        warehouse.getDistricts().entrySet().parallelStream()
            .map(d -> modelMapper.map(d.getValue(), DistrictRepresentation.class))
            .collect(Collectors.toList());

    return ResponseEntity.ok(districtRepresentations);
  }

  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId) {
    WarehouseData warehouse = dataRoot.getWarehouses().get(warehouseId);
    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    List<StockRepresentation> stockRepresentations =
        warehouse.getStocks().parallelStream()
            .map(s -> modelMapper.map(s, StockRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(stockRepresentations);
  }

  @Override
  public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      String warehouseId, String districtId) {
    WarehouseData warehouse = dataRoot.getWarehouses().get(warehouseId);
    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    DistrictData district = warehouse.getDistricts().get(districtId);
    if (district == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    List<CustomerRepresentation> customerRepresentations =
        district.getCustomers().parallelStream()
            .map(c -> modelMapper.map(c, CustomerRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(customerRepresentations);
  }

  @Override
  public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      String warehouseId, String districtId) {
    WarehouseData warehouse = dataRoot.getWarehouses().get(warehouseId);
    if (warehouse == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    DistrictData district = warehouse.getDistricts().get(districtId);
    if (district == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    List<OrderRepresentation> orderRepresentations =
        district.getOrders().entrySet().parallelStream()
            .map(o -> modelMapper.map(o.getValue(), OrderRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(orderRepresentations);
  }

  @Override
  public ResponseEntity<List<CarrierRepresentation>> getCarriers() {
    return ResponseEntity.ok(
        dataRoot.getCarriers().entrySet().parallelStream()
            .map(c -> modelMapper.map(c.getValue(), CarrierRepresentation.class))
            .collect(Collectors.toList()));
  }
}
