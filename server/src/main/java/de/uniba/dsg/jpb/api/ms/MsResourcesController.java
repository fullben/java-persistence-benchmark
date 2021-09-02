package de.uniba.dsg.jpb.api.ms;

import de.uniba.dsg.jpb.api.ResourcesController;
import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.StockData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.representations.CarrierRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.CustomerRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.DistrictRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.EmployeeRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.OrderRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.ProductRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.StockRepresentation;
import de.uniba.dsg.jpb.data.transfer.representations.WarehouseRepresentation;
import java.util.List;
import java.util.stream.Collectors;
import org.jacis.store.JacisStore;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides read-only access to many of the resources managed by the server when
 * launched in MS persistence mode.
 *
 * @author Benedikt Full
 */
@RestController
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsResourcesController implements ResourcesController {

  private final JacisStore<String, CarrierData> carrierStore;
  private final JacisStore<String, WarehouseData> warehouseStore;
  private final JacisStore<String, DistrictData> districtStore;
  private final JacisStore<String, StockData> stockStore;
  private final JacisStore<String, CustomerData> customerStore;
  private final JacisStore<String, OrderData> orderStore;
  private final JacisStore<String, EmployeeData> employeeStore;
  private final JacisStore<String, ProductData> productStore;
  private final ModelMapper modelMapper;

  public MsResourcesController(
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
  public Iterable<ProductRepresentation> getProducts() {
    return productStore.getAllReadOnly().parallelStream()
        .map(p -> modelMapper.map(p, ProductRepresentation.class))
        .collect(Collectors.toList());
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
  public List<WarehouseRepresentation> getWarehouses() {
    return warehouseStore
        .streamReadOnly()
        .map(w -> modelMapper.map(w, WarehouseRepresentation.class))
        .collect(Collectors.toList());
  }

  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId) {
    if (!warehouseStore.containsKey(warehouseId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return districtStore.computeAtomic(
        () -> {
          List<DistrictRepresentation> districts =
              districtStore
                  .streamReadOnly(d -> d.getWarehouseId().equals(warehouseId))
                  .map(d -> modelMapper.map(d, DistrictRepresentation.class))
                  .collect(Collectors.toList());
          return ResponseEntity.ok(districts);
        });
  }

  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId) {
    if (!warehouseStore.containsKey(warehouseId)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return stockStore.computeAtomic(
        () -> {
          List<StockRepresentation> stocks =
              stockStore
                  .streamReadOnly(s -> s.getWarehouseId().equals(warehouseId))
                  .map(s -> modelMapper.map(s, StockRepresentation.class))
                  .collect(Collectors.toList());
          return ResponseEntity.ok(stocks);
        });
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
            .map(o -> modelMapper.map(o, OrderRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(orders);
  }

  @Override
  public List<CarrierRepresentation> getCarriers() {
    return carrierStore
        .streamReadOnly()
        .map(c -> modelMapper.map(c, CarrierRepresentation.class))
        .collect(Collectors.toList());
  }
}
