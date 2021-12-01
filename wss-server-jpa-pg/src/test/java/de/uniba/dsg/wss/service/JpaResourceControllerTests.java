package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import de.uniba.dsg.wss.api.JpaResourceController;
import de.uniba.dsg.wss.data.access.CarrierRepository;
import de.uniba.dsg.wss.data.access.OrderRepository;
import de.uniba.dsg.wss.data.access.ProductRepository;
import de.uniba.dsg.wss.data.access.WarehouseRepository;
import de.uniba.dsg.wss.data.gen.JpaDataConverter;
import de.uniba.dsg.wss.data.gen.TestDataGenerator;
import de.uniba.dsg.wss.data.transfer.representations.CustomerRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.DistrictRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.OrderRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.ProductRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.StockRepresentation;
import de.uniba.dsg.wss.data.transfer.representations.WarehouseRepresentation;
import java.util.List;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class JpaResourceControllerTests {

  @Autowired private JpaResourceController controller;
  @Autowired private WarehouseRepository warehouseRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private CarrierRepository carrierRepository;
  @Autowired private OrderRepository orderRepository;

  @BeforeEach
  public void setUp() {
    TestDataGenerator generator = new TestDataGenerator();
    generator.generate();
    JpaDataConverter converter = new JpaDataConverter();
    converter.convert(generator);

    productRepository.saveAll(converter.getProducts());
    carrierRepository.saveAll(converter.getCarriers());
    warehouseRepository.saveAll(converter.getWarehouses());
  }

  @AfterEach
  public void tearDown() {
    orderRepository.deleteAll();
    warehouseRepository.deleteAll();
    productRepository.deleteAll();
    carrierRepository.deleteAll();
  }

  @Test
  public void checkWarehouse() {
    ResponseEntity<List<WarehouseRepresentation>> warehouses = controller.getWarehouses();
    assertEquals(5, warehouses.getBody().size());
  }

  @Test
  public void checkWarehouseDistricts() {
    ResponseEntity<List<DistrictRepresentation>> districts = controller.getWarehouseDistricts("W0");
    assertEquals(2, districts.getBody().size());
    assertFalse(districts.getBody().stream().noneMatch(d -> "D0".equals(d.getId())));
  }

  @Test
  public void checkProducts() {
    ResponseEntity<Iterable<ProductRepresentation>> products = controller.getProducts();
    assertEquals(10, IterableUtil.sizeOf(products.getBody()));
  }

  @Test
  public void checkWarehouseStocks() {
    ResponseEntity<List<StockRepresentation>> stocks = controller.getWarehouseStocks("W0");
    assertEquals(5, stocks.getBody().size());
  }

  @Test
  public void checkDistrictCustomers() {
    ResponseEntity<List<CustomerRepresentation>> customers =
        controller.getDistrictCustomers("W0", "D0");
    assertEquals(2, customers.getBody().size());
  }

  @Test
  public void checkOrderPerDistrict() {
    ResponseEntity<List<OrderRepresentation>> orders = controller.getDistrictOrders("W0", "D0");
    assertEquals(2, orders.getBody().size());
  }

  @Test
  public void checkCarriers() {
    ResponseEntity<List<OrderRepresentation>> orders = controller.getDistrictOrders("W0", "D0");
    assertEquals(2, orders.getBody().size());
  }
}
