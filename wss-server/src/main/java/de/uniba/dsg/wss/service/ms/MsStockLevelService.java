package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.model.ms.DistrictData;
import de.uniba.dsg.wss.data.model.ms.OrderData;
import de.uniba.dsg.wss.data.model.ms.OrderItemData;
import de.uniba.dsg.wss.data.model.ms.StockData;
import de.uniba.dsg.wss.data.model.ms.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.wss.service.StockLevelService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.jacis.store.JacisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsStockLevelService extends StockLevelService {

  private final JacisStore<String, WarehouseData> warehouseStore;
  private final JacisStore<String, DistrictData> districtStore;
  private final JacisStore<String, OrderData> orderStore;
  private final JacisStore<String, OrderItemData> orderItemStore;
  private final JacisStore<String, StockData> stockStore;

  @Autowired
  public MsStockLevelService(
      JacisStore<String, WarehouseData> warehouseStore,
      JacisStore<String, DistrictData> districtStore,
      JacisStore<String, OrderData> orderStore,
      JacisStore<String, OrderItemData> orderItemStore,
      JacisStore<String, StockData> stockStore) {
    this.warehouseStore = warehouseStore;
    this.districtStore = districtStore;
    this.orderStore = orderStore;
    this.orderItemStore = orderItemStore;
    this.stockStore = stockStore;
  }

  @Override
  public StockLevelResponse process(StockLevelRequest req) {
    // Fetch warehouse and district
    WarehouseData warehouse = warehouseStore.getReadOnly(req.getWarehouseId());
    DistrictData district = districtStore.getReadOnly(req.getDistrictId());
    // Find the most 20 recent orders for the district
    List<String> orderIds =
        orderStore
            .streamReadOnly(o -> o.getDistrictId().equals(district.getId()))
            .parallel()
            .sorted(Comparator.comparing(OrderData::getEntryDate))
            .limit(20)
            .map(OrderData::getId)
            .collect(Collectors.toList());

    // Find the corresponding stock objects and count the ones below the given threshold
    List<String> productIds =
        orderItemStore
            .streamReadOnly(i -> orderIds.contains(i.getOrderId()))
            .parallel()
            .map(OrderItemData::getProductId)
            .distinct()
            .collect(Collectors.toList());
    int lowStockCount =
        (int)
            stockStore
                .streamReadOnly(s -> s.getWarehouseId().equals(warehouse.getId()))
                .parallel()
                .filter(
                    s ->
                        productIds.contains(s.getProductId())
                            && s.getQuantity() < req.getStockThreshold())
                .count();

    StockLevelResponse res = new StockLevelResponse(req);
    res.setLowStocksCount(lowStockCount);
    return res;
  }
}
