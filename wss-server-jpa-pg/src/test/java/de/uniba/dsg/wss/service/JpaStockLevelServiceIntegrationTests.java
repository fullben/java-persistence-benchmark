package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.uniba.dsg.wss.data.access.CarrierRepository;
import de.uniba.dsg.wss.data.access.OrderRepository;
import de.uniba.dsg.wss.data.access.ProductRepository;
import de.uniba.dsg.wss.data.access.StockRepository;
import de.uniba.dsg.wss.data.access.WarehouseRepository;
import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.data.gen.JpaDataConverter;
import de.uniba.dsg.wss.data.model.DistrictEntity;
import de.uniba.dsg.wss.data.model.OrderEntity;
import de.uniba.dsg.wss.data.model.WarehouseEntity;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DataJpaTest
public class JpaStockLevelServiceIntegrationTests {

  @Autowired private WarehouseRepository warehouseRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private CarrierRepository carrierRepository;
  @Autowired private StockRepository stockRepository;
  private JpaStockLevelService stockLevelService;
  private StockLevelRequest request;
  private long lowStocksCount;

  @BeforeEach
  public void setUp() {
    DataGenerator generator =
        new DataGenerator(1, 1, 100, 100, 1_000, (pw) -> new BCryptPasswordEncoder().encode(pw));
    generator.generate();
    JpaDataConverter converter = new JpaDataConverter();
    converter.convert(generator);

    productRepository.saveAll(converter.getProducts());
    carrierRepository.saveAll(converter.getCarriers());
    warehouseRepository.saveAll(converter.getWarehouses());

    int threshold = 15;
    WarehouseEntity warehouse = warehouseRepository.findAll().get(0);
    DistrictEntity district = warehouse.getDistricts().get(0);
    Set<String> productIds =
        district.getOrders().stream()
            .sorted(Comparator.comparing(OrderEntity::getEntryDate))
            .skip(Math.max(0, district.getOrders().size() - 20))
            .flatMap(o -> o.getItems().stream().map(i -> i.getProduct().getId()))
            .collect(Collectors.toSet());

    lowStocksCount =
        warehouse.getStocks().parallelStream()
            .filter(s -> productIds.contains(s.getProduct().getId()) && s.getQuantity() < threshold)
            .count();
    request = new StockLevelRequest();
    request.setWarehouseId(warehouse.getId());
    request.setDistrictId(district.getId());
    request.setStockThreshold(threshold);

    stockLevelService = new JpaStockLevelService(orderRepository, stockRepository);
  }

  @Test
  public void processingReturnsExpectedValues() {
    StockLevelResponse res = stockLevelService.process(request);

    assertEquals(request.getWarehouseId(), res.getWarehouseId());
    assertEquals(request.getDistrictId(), res.getDistrictId());
    assertEquals(lowStocksCount, res.getLowStocksCount());
  }

  @AfterEach
  public void tearDown() {
    warehouseRepository.deleteAll();
    productRepository.deleteAll();
    carrierRepository.deleteAll();
  }
}
