package de.uniba.dsg.wss.api;

import de.uniba.dsg.wss.data.transfer.representations.CarrierRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.CustomerRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.DistrictRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.EmployeeRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.OrderRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.ProductRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.StockRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.WarehouseRepresentation;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Implementations of this controller provide read-only access to many of the resources maintained
 * by this server.
 *
 * @author Benedikt Full
 */
@RequestMapping("api")
@Validated
public interface ResourceController {

  @GetMapping(value = "products", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(summary = "Returns products", description = "Returns all products.")
  ResponseEntity<Iterable<ProductRepresentation>> getProducts();

  @GetMapping(value = "employees/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(
      summary = "Returns an employee",
      description = "Finds and returns the employee identified by the given username.")
  ResponseEntity<EmployeeRepresentation> getEmployee(@NotBlank @PathVariable String username);

  @GetMapping(value = "warehouses", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(summary = "Returns warehouses", description = "Returns all warehouses.")
  ResponseEntity<List<WarehouseRepresentation>> getWarehouses();

  @GetMapping(
      value = "warehouses/{warehouseId}/districts",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(
      summary = "Returns warehouse districts",
      description = "Returns the 10 districts of the specified warehouse.")
  ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(
      @NotBlank @PathVariable String warehouseId);

  @GetMapping(
      value = "warehouses/{warehouseId}/stocks",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(
      summary = "Returns warehouse stocks",
      description = "Returns the stocks of the specified warehouse.")
  ResponseEntity<List<StockRepresentation>> getWarehouseStocks(
      @NotBlank @PathVariable String warehouseId);

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/customers",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(
      summary = "Returns warehouse customers",
      description = "Returns the customers of the specified district.")
  ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      @NotBlank @PathVariable String warehouseId, @NotBlank @PathVariable String districtId);

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/orders",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(
      summary = "Returns district orders",
      description = "Returns the orders of the specified district.")
  ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      @NotBlank @PathVariable String warehouseId, @NotBlank @PathVariable String districtId);

  @GetMapping(value = "carriers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(summary = "Returns carriers", description = "Returns all carriers.")
  ResponseEntity<List<CarrierRepresentation>> getCarriers();
}
