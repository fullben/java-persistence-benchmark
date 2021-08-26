package de.uniba.dsg.jpb.service.ms;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.Find;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.jpb.service.StockLevelService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsStockLevelService extends StockLevelService {

  private final DataManager dataManager;

  public MsStockLevelService(DataManager dataManager) {
    this.dataManager = dataManager;
  }

  @Override
  public StockLevelResponse process(StockLevelRequest req) {
    return dataManager.read(
        (root) -> {
          // Fetch warehouse, district, and customer (either by id or email)
          WarehouseData warehouse =
              Find.warehouseById(req.getWarehouseId(), root.findAllWarehouses());
          DistrictData district = Find.districtById(req.getDistrictId(), warehouse);
          // Find the most 20 recent orders for the district
          List<OrderData> orders = Find.twentyMostRecentOrdersOfDistrict(district);

          // Find the corresponding stock objects and count the ones below the given threshold
          List<String> productIds =
              orders.stream()
                  .flatMap(o -> o.getItems().stream().map(i -> i.getProduct().getId()))
                  .distinct()
                  .collect(Collectors.toList());
          int lowStockCount =
              Find.stocksByProductIdsAndQuantityThreshold(
                      productIds, req.getStockThreshold(), warehouse.getStocks())
                  .size();

          StockLevelResponse res = new StockLevelResponse(req);
          res.setLowStocksCount(lowStockCount);
          return res;
        });
  }
}
