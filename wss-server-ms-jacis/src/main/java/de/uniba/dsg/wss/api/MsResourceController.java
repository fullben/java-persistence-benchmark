package de.uniba.dsg.wss.api;

import de.uniba.dsg.wss.data.model.EmployeeData;
import de.uniba.dsg.wss.data.transfer.representations.CarrierRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.CustomerRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.DistrictRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.EmployeeRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.OrderRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.ProductRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.StockRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.WarehouseRepresentation;
import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.ProductData;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.List;
import java.util.stream.Collectors;
import org.jacis.store.JacisStore;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides read-only access to many of the resources managed by the server.
 *
 * @author Benedikt Full
 */
@RestController
public class MsResourceController implements ResourceController {

  private final JacisStore<String, CarrierData> carrierStore;
  private final JacisStore<String, WarehouseData> warehouseStore;
  private final JacisStore<String, DistrictData> districtStore;
  private final JacisStore<String, StockData> stockStore;
  private final JacisStore<String, CustomerData> customerStore;
  private final JacisStore<String, OrderData> orderStore;
  private final JacisStore<String, EmployeeData> employeeStore;
  private final JacisStore<String, ProductData> productStore;
  private final ModelMapper modelMapper;

  public MsResourceController(
      JacisStore<String, CarrierData> carrierStore,
      JacisStore<String, WarehouseData> warehouseStore,
      JacisStore<String, DistrictData> districtStore,
      JacisStore<String, StockData> stockStore,
      JacisStore<String, CustomerData> customerStore,
      JacisStore<String, OrderData> orderStore,
      JacisStore<String, EmployeeData> employeeStore,
      JacisStore<String, ProductData> productStore) {
    this.carrierStore = carrierStore;
    this.warehouseStore = warehouseStore;
    this.districtStore = districtStore;
    this.stockStore = stockStore;
    this.customerStore = customerStore;
    this.orderStore = orderStore;
    this.employeeStore = employeeStore;
    this.productStore = productStore;
    modelMapper = new ModelMapper();
  }

  @Override
  public ResponseEntity<Iterable<ProductRepresentation>> getProducts() {
    List<ProductRepresentation> products =
        productStore
            .streamReadOnly()
            .parallel()
            .map(p -> modelMapper.map(p, ProductRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(products);
  }

  @Override
  public ResponseEntity<EmployeeRepresentation> getEmployee(String username) {
    EmployeeData employee =
        employeeStore.streamReadOnly(e -> e.getUsername().equals(username)).findAny().orElse(null);
    if (employee == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(modelMapper.map(employee, EmployeeRepresentation.class));
  }

  @Override
  public ResponseEntity<List<WarehouseRepresentation>> getWarehouses() {
    List<WarehouseRepresentation> warehouses =
        warehouseStore
            .streamReadOnly()
            .map(w -> modelMapper.map(w, WarehouseRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(warehouses);
  }

  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId) {
    if (!warehouseStore.containsKey(warehouseId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    List<DistrictRepresentation> districts =
        districtStore
            .streamReadOnly(d -> d.getWarehouseId().equals(warehouseId))
            .parallel()
            .map(d -> modelMapper.map(d, DistrictRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(districts);
  }

  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId) {
    if (!warehouseStore.containsKey(warehouseId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    List<StockRepresentation> stocks =
        stockStore
            .streamReadOnly(s -> s.getWarehouseId().equals(warehouseId))
            .parallel()
            .map(s -> modelMapper.map(s, StockRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(stocks);
  }

  @Override
  public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      String warehouseId, String districtId) {
    if (!warehouseStore.containsKey(warehouseId) || !districtStore.containsKey(districtId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    List<CustomerRepresentation> customers =
        customerStore
            .streamReadOnly(c -> c.getDistrictId().equals(districtId))
            .parallel()
            .map(c -> modelMapper.map(c, CustomerRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(customers);
  }

  @Override
  public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      String warehouseId, String districtId) {
    if (!warehouseStore.containsKey(warehouseId) || !districtStore.containsKey(districtId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    List<OrderRepresentation> orders =
        orderStore
            .streamReadOnly(o -> o.getDistrictId().equals(districtId))
            .parallel()
            .map(o -> modelMapper.map(o, OrderRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(orders);
  }

  @Override
  public ResponseEntity<List<CarrierRepresentation>> getCarriers() {
    List<CarrierRepresentation> carriers =
        carrierStore
            .streamReadOnly()
            .map(c -> modelMapper.map(c, CarrierRepresentation.class))
            .collect(Collectors.toList());
    ResponseEntity.ok(carriers);
  }
}
