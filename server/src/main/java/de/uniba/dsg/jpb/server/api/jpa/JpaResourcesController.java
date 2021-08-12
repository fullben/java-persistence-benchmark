package de.uniba.dsg.jpb.server.api.jpa;

import de.uniba.dsg.jpb.server.data.model.jpa.CarrierEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.CustomerEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.DistrictEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.OrderEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.ProductEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.StockEntity;
import de.uniba.dsg.jpb.server.data.model.jpa.WarehouseEntity;
import de.uniba.dsg.jpb.server.data.access.jpa.CarrierRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.CustomerRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.DistrictRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.OrderRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.ProductRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.StockRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.WarehouseRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class JpaResourcesController {

  private final ProductRepository productRepository;
  private final CarrierRepository carrierRepository;
  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final StockRepository stockRepository;
  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;

  @Autowired
  public JpaResourcesController(
      ProductRepository productRepository,
      CarrierRepository carrierRepository,
      WarehouseRepository warehouseRepository,
      DistrictRepository districtRepository,
      StockRepository stockRepository,
      CustomerRepository customerRepository,
      OrderRepository orderRepository) {
    this.productRepository = productRepository;
    this.carrierRepository = carrierRepository;
    this.warehouseRepository = warehouseRepository;
    this.districtRepository = districtRepository;
    this.stockRepository = stockRepository;
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
  }

  @GetMapping(value = "products", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Page<ProductEntity> getProducts(@RequestParam int page, @RequestParam int size) {
    return productRepository.findAll(PageRequest.of(page, size));
  }

  @GetMapping(value = "warehouses", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<WarehouseEntity> getWarehouses() {
    return warehouseRepository.findAll();
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/districts",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<DistrictEntity> getWarehouseDistricts(@PathVariable Long warehouseId) {
    return districtRepository.findByWarehouseId(warehouseId);
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/stocks",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<StockEntity> getWarehouseStocks(@PathVariable Long warehouseId) {
    return stockRepository.findByWarehouseId(warehouseId);
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/customers",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<CustomerEntity>> getDistrictCustomers(
      @PathVariable Long warehouseId, @PathVariable Long districtId) {
    List<CustomerEntity> customers = customerRepository.findByDistrictId(districtId);
    if (customers.parallelStream()
        .anyMatch(c -> !c.getDistrict().getWarehouse().getId().equals(warehouseId))) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(customers);
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/orders",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<OrderEntity>> getDistrictOrders(
      @PathVariable Long warehouseId, @PathVariable Long districtId) {
    List<OrderEntity> orders = orderRepository.findByDistrictId(districtId);
    if (orders.stream()
        .anyMatch(o -> !o.getDistrict().getWarehouse().getId().equals(warehouseId))) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(orderRepository.findByDistrictId(districtId));
  }

  @GetMapping(value = "carriers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<CarrierEntity> getCarriers() {
    return carrierRepository.findAll();
  }
}
