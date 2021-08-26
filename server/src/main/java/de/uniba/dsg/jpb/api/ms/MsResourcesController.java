package de.uniba.dsg.jpb.api.ms;

import de.uniba.dsg.jpb.api.ResourcesController;
import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
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
import javax.validation.constraints.NotBlank;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
@Validated
public class MsResourcesController implements ResourcesController {

  private final DataManager dataManager;
  private final ModelMapper modelMapper;

  @Autowired
  public MsResourcesController(DataManager dataManager) {
    this.dataManager = dataManager;
    modelMapper = new ModelMapper();
  }

  @GetMapping(value = "products", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public Iterable<ProductRepresentation> getProducts() {
    return dataManager.read(
        (root) -> {
          return root.findAllProducts().parallelStream()
              .map(p -> modelMapper.map(p, ProductRepresentation.class))
              .collect(Collectors.toList());
        });
  }

  @GetMapping(value = "employees/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public ResponseEntity<EmployeeRepresentation> getEmployee(
      @NotBlank @PathVariable String username) {
    return dataManager.read(
        (root) -> {
          EmployeeData employee = root.findEmployeeByUsername(username);
          if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
          }
          return ResponseEntity.ok(modelMapper.map(employee, EmployeeRepresentation.class));
        });
  }

  @GetMapping(value = "warehouses", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public List<WarehouseRepresentation> getWarehouses() {
    return dataManager.read(
        (root) -> {
          return root.findAllWarehouses().stream()
              .map(w -> modelMapper.map(w, WarehouseRepresentation.class))
              .collect(Collectors.toList());
        });
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/districts",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(
      @NotBlank @PathVariable String warehouseId) {
    return dataManager.read(
        (root) -> {
          WarehouseData warehouse = root.findWarehouseById(warehouseId);
          if (warehouse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
          }

          List<DistrictRepresentation> districts =
              warehouse.getDistricts().stream()
                  .map(d -> modelMapper.map(d, DistrictRepresentation.class))
                  .collect(Collectors.toList());
          return ResponseEntity.ok(districts);
        });
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/stocks",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(
      @NotBlank @PathVariable String warehouseId) {
    return dataManager.read(
        (root) -> {
          WarehouseData warehouse = root.findWarehouseById(warehouseId);
          if (warehouse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
          }

          List<StockRepresentation> stocks =
              warehouse.getStocks().stream()
                  .map(s -> modelMapper.map(s, StockRepresentation.class))
                  .collect(Collectors.toList());
          return ResponseEntity.ok(stocks);
        });
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/customers",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      @NotBlank @PathVariable String warehouseId, @NotBlank @PathVariable String districtId) {
    return dataManager.read(
        (root) -> {
          WarehouseData warehouse = root.findWarehouseById(warehouseId);
          if (warehouse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
          }

          DistrictData district =
              warehouse.getDistricts().stream()
                  .filter(d -> d.getId().equals(districtId))
                  .findAny()
                  .orElse(null);
          if (district == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
          }

          List<CustomerRepresentation> customers =
              district.getCustomers().stream()
                  .map(c -> modelMapper.map(c, CustomerRepresentation.class))
                  .collect(Collectors.toList());
          return ResponseEntity.ok(customers);
        });
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/orders",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      @NotBlank @PathVariable String warehouseId, @NotBlank @PathVariable String districtId) {
    return dataManager.read(
        (root) -> {
          WarehouseData warehouse = root.findWarehouseById(warehouseId);
          if (warehouse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
          }

          DistrictData district =
              warehouse.getDistricts().stream()
                  .filter(d -> d.getId().equals(districtId))
                  .findAny()
                  .orElse(null);
          if (district == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
          }

          List<OrderRepresentation> orders =
              district.getOrders().stream()
                  .map(o -> modelMapper.map(o, OrderRepresentation.class))
                  .collect(Collectors.toList());
          return ResponseEntity.ok(orders);
        });
  }

  @GetMapping(value = "carriers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public List<CarrierRepresentation> getCarriers() {
    return dataManager.read(
        (root) -> {
          return root.findAllCarriers().stream()
              .map(w -> modelMapper.map(w, CarrierRepresentation.class))
              .collect(Collectors.toList());
        });
  }
}
