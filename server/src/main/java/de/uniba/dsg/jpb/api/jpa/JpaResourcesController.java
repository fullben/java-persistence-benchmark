package de.uniba.dsg.jpb.api.jpa;

import de.uniba.dsg.jpb.api.ResourcesController;
import de.uniba.dsg.jpb.data.access.jpa.CarrierRepository;
import de.uniba.dsg.jpb.data.access.jpa.CustomerRepository;
import de.uniba.dsg.jpb.data.access.jpa.DistrictRepository;
import de.uniba.dsg.jpb.data.access.jpa.EmployeeRepository;
import de.uniba.dsg.jpb.data.access.jpa.OrderRepository;
import de.uniba.dsg.jpb.data.access.jpa.ProductRepository;
import de.uniba.dsg.jpb.data.access.jpa.StockRepository;
import de.uniba.dsg.jpb.data.access.jpa.WarehouseRepository;
import de.uniba.dsg.jpb.data.model.jpa.CustomerEntity;
import de.uniba.dsg.jpb.data.model.jpa.EmployeeEntity;
import de.uniba.dsg.jpb.data.model.jpa.OrderEntity;
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
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
@Validated
public class JpaResourcesController implements ResourcesController {

  private final ProductRepository productRepository;
  private final CarrierRepository carrierRepository;
  private final WarehouseRepository warehouseRepository;
  private final EmployeeRepository employeeRepository;
  private final DistrictRepository districtRepository;
  private final StockRepository stockRepository;
  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final ModelMapper modelMapper;

  @Autowired
  public JpaResourcesController(
      ProductRepository productRepository,
      CarrierRepository carrierRepository,
      WarehouseRepository warehouseRepository,
      EmployeeRepository employeeRepository,
      DistrictRepository districtRepository,
      StockRepository stockRepository,
      CustomerRepository customerRepository,
      OrderRepository orderRepository) {
    this.productRepository = productRepository;
    this.carrierRepository = carrierRepository;
    this.warehouseRepository = warehouseRepository;
    this.employeeRepository = employeeRepository;
    this.districtRepository = districtRepository;
    this.stockRepository = stockRepository;
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
    modelMapper = new ModelMapper();
  }

  @GetMapping(value = "products", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public Iterable<ProductRepresentation> getProducts() {
    return productRepository.findAll().stream()
        .map(p -> modelMapper.map(p, ProductRepresentation.class))
        .collect(Collectors.toList());
  }

  @GetMapping(value = "employees/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public ResponseEntity<EmployeeRepresentation> getEmployee(
      @NotBlank @PathVariable String username) {
    EmployeeEntity employee = employeeRepository.findByUsername(username).orElse(null);
    if (employee == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(modelMapper.map(employee, EmployeeRepresentation.class));
  }

  @GetMapping(value = "warehouses", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public List<WarehouseRepresentation> getWarehouses() {
    return warehouseRepository.findAll().stream()
        .map(w -> modelMapper.map(w, WarehouseRepresentation.class))
        .collect(Collectors.toList());
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/districts",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(
      @NotBlank @PathVariable String warehouseId) {
    List<DistrictRepresentation> districts =
        districtRepository.findByWarehouseId(warehouseId).stream()
            .map(d -> modelMapper.map(d, DistrictRepresentation.class))
            .collect(Collectors.toList());
    if (districts.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(districts);
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/stocks",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(
      @NotBlank @PathVariable String warehouseId) {
    List<StockRepresentation> stocks =
        stockRepository.findByWarehouseId(warehouseId).stream()
            .map(s -> modelMapper.map(s, StockRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(stocks);
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/customers",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      @NotBlank @PathVariable String warehouseId, @NotBlank @PathVariable String districtId) {
    List<CustomerEntity> customers = customerRepository.findByDistrictId(districtId);
    if (customers.parallelStream()
        .anyMatch(c -> !c.getDistrict().getWarehouse().getId().equals(warehouseId))) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    List<CustomerRepresentation> customerReps =
        customers.stream()
            .map(c -> modelMapper.map(c, CustomerRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(customerReps);
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/orders",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      @NotBlank @PathVariable String warehouseId, @NotBlank @PathVariable String districtId) {
    List<OrderEntity> orders = orderRepository.findByDistrictId(districtId);
    if (orders.stream()
        .anyMatch(o -> !o.getDistrict().getWarehouse().getId().equals(warehouseId))) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    List<OrderRepresentation> orderReps =
        orders.stream()
            .map(o -> modelMapper.map(o, OrderRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(orderReps);
  }

  @GetMapping(value = "carriers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public List<CarrierRepresentation> getCarriers() {
    return carrierRepository.findAll().stream()
        .map(c -> modelMapper.map(c, CarrierRepresentation.class))
        .collect(Collectors.toList());
  }
}
