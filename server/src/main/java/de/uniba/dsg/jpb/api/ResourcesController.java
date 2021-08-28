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
public interface ResourcesController {

  @GetMapping(value = "products", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  Iterable<ProductRepresentation> getProducts();

  @GetMapping(value = "employees/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<EmployeeRepresentation> getEmployee(@NotBlank @PathVariable String username);

  @GetMapping(value = "warehouses", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  List<WarehouseRepresentation> getWarehouses();

  @GetMapping(
      value = "warehouses/{warehouseId}/districts",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(
      @NotBlank @PathVariable String warehouseId);

  @GetMapping(
      value = "warehouses/{warehouseId}/stocks",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<List<StockRepresentation>> getWarehouseStocks(
      @NotBlank @PathVariable String warehouseId);

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/customers",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      @NotBlank @PathVariable String warehouseId, @NotBlank @PathVariable String districtId);

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/orders",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      @NotBlank @PathVariable String warehouseId, @NotBlank @PathVariable String districtId);

  @GetMapping(value = "carriers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  List<CarrierRepresentation> getCarriers();
}
