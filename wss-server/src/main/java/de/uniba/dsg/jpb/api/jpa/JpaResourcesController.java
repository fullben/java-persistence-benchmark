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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides read-only access to many of the resources managed by the server when
 * launched in JPA persistence mode.
 *
 * @author Benedikt Full
 */
@RestController
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
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

  @Override
  public Iterable<ProductRepresentation> getProducts() {
    return productRepository.findAll().stream()
        .map(p -> modelMapper.map(p, ProductRepresentation.class))
        .collect(Collectors.toList());
  }

  @Override
  public ResponseEntity<EmployeeRepresentation> getEmployee(String username) {
    EmployeeEntity employee = employeeRepository.findByUsername(username).orElse(null);
    if (employee == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(modelMapper.map(employee, EmployeeRepresentation.class));
  }

  @Override
  public List<WarehouseRepresentation> getWarehouses() {
    return warehouseRepository.findAll().stream()
        .map(w -> modelMapper.map(w, WarehouseRepresentation.class))
        .collect(Collectors.toList());
  }

  @Override
  public ResponseEntity<List<DistrictRepresentation>> getWarehouseDistricts(String warehouseId) {
    List<DistrictRepresentation> districts =
        districtRepository.findByWarehouseId(warehouseId).stream()
            .map(d -> modelMapper.map(d, DistrictRepresentation.class))
            .collect(Collectors.toList());
    if (districts.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(districts);
  }

  @Override
  public ResponseEntity<List<StockRepresentation>> getWarehouseStocks(String warehouseId) {
    List<StockRepresentation> stocks =
        stockRepository.findByWarehouseId(warehouseId).stream()
            .map(s -> modelMapper.map(s, StockRepresentation.class))
            .collect(Collectors.toList());
    return ResponseEntity.ok(stocks);
  }

  @Override
  public ResponseEntity<List<CustomerRepresentation>> getDistrictCustomers(
      String warehouseId, String districtId) {
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

  @Override
  public ResponseEntity<List<OrderRepresentation>> getDistrictOrders(
      String warehouseId, String districtId) {
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

  @Override
  public List<CarrierRepresentation> getCarriers() {
    return carrierRepository.findAll().stream()
        .map(c -> modelMapper.map(c, CarrierRepresentation.class))
        .collect(Collectors.toList());
  }
}