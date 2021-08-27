package de.uniba.dsg.jpb.service.ms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelResponse;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MsStockLevelServiceIntegrationTests extends MicroStreamServiceTest {

  private DataManager dataManager;
  private MsStockLevelService stockLevelService;
  private StockLevelRequest request;
  private long lowStocksCount;

  @BeforeEach
  public void setUp() {
    populateStorage(new JpaDataGenerator(2, 1, 10, 10, 1_000, new BCryptPasswordEncoder()));
    dataManager = dataManager();
    request = new StockLevelRequest();

    int threshold = 15;
    dataManager.read(
        (root) -> {
          WarehouseData warehouse = root.findAllWarehouses().get(0);
          DistrictData district = warehouse.getDistricts().get(0);
          request.setWarehouseId(warehouse.getId());
          request.setDistrictId(district.getId());
          request.setStockThreshold(threshold);

          Set<String> productIds =
              district.getOrders().stream()
                  .sorted(Comparator.comparing(OrderData::getEntryDate))
                  .skip(Math.max(0, district.getOrders().size() - 20))
                  .flatMap(o -> o.getItems().stream().map(i -> i.getProduct().getId()))
                  .collect(Collectors.toSet());

          lowStocksCount =
              warehouse.getStocks().parallelStream()
                  .filter(
                      s ->
                          productIds.contains(s.getProduct().getId())
                              && s.getQuantity() < threshold)
                  .count();
        });

    stockLevelService = new MsStockLevelService(dataManager);
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
    dataManager.close();
    clearStorage();
  }
}
