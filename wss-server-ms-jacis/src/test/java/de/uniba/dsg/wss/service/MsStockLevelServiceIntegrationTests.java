package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.data.gen.MsDataWriter;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.OrderItemData;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jacis.container.JacisContainer;
import org.jacis.store.JacisStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MsStockLevelServiceIntegrationTests extends MicroStreamServiceTest {

  @Autowired private JacisContainer container;
  @Autowired private JacisStore<String, WarehouseData> warehouseStore;
  @Autowired private JacisStore<String, StockData> stockStore;
  @Autowired private JacisStore<String, DistrictData> districtStore;
  @Autowired private JacisStore<String, OrderData> orderStore;
  @Autowired private JacisStore<String, OrderItemData> orderItemStore;
  @Autowired private MsDataWriter dataWriter;
  private MsStockLevelService stockLevelService;
  private StockLevelRequest request;
  private long lowStocksCount;

  @BeforeEach
  public void setUp() {
    populateStorage(
        new DataGenerator(2, 1, 10, 10, 1_000, (pw) -> new BCryptPasswordEncoder().encode(pw)),
        dataWriter);
    request = new StockLevelRequest();

    int threshold = 15;
    WarehouseData warehouse = warehouseStore.getAllReadOnly().get(0);
    DistrictData district =
        districtStore
            .streamReadOnly(d -> d.getWarehouseId().equals(warehouse.getId()))
            .collect(Collectors.toList())
            .get(0);
    request.setWarehouseId(warehouse.getId());
    request.setDistrictId(district.getId());
    request.setStockThreshold(threshold);

    List<OrderData> districtOrders =
        orderStore
            .streamReadOnly(o -> o.getDistrictId().equals(district.getId()))
            .collect(Collectors.toList());
    Set<String> productIds =
        districtOrders.stream()
            .sorted(Comparator.comparing(OrderData::getEntryDate))
            .skip(Math.max(0, districtOrders.size() - 20))
            .flatMap(
                o ->
                    orderItemStore
                        .streamReadOnly(i -> i.getOrderId().equals(o.getId()))
                        .map(OrderItemData::getProductId))
            .collect(Collectors.toSet());

    lowStocksCount =
        stockStore
            .streamReadOnly(s -> s.getWarehouseId().equals(warehouse.getId()))
            .filter(s -> productIds.contains(s.getProductId()) && s.getQuantity() < threshold)
            .count();

    stockLevelService =
        new MsStockLevelService(
            container, warehouseStore, districtStore, orderStore, orderItemStore, stockStore);
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
    container.clearAllStores();
  }
}
