package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.access.ms.DataConsistencyManager;
import de.uniba.dsg.wss.data.model.ms.*;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.wss.service.StockLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the mentioned transactions in the README.
 *
 * @author Johannes Manner
 */
@Service
public class MsStockLevelService extends StockLevelService {

  private final MsDataRoot dataRoot;
  private final DataConsistencyManager consistencyManager;

  @Autowired
  public MsStockLevelService(MsDataRoot dataRoot, DataConsistencyManager consistencyManager){
    this.dataRoot = dataRoot;
    this.consistencyManager = consistencyManager;
  }

  @Override
  public StockLevelResponse process(StockLevelRequest req) {
    WarehouseData warehouse = this.dataRoot.getWarehouses().get(req.getWarehouseId());
    DistrictData district = warehouse.getDistricts().get(req.getDistrictId());
    List<StockData> stocksInOrder = district.getOrders().entrySet().stream()
            .map(entry -> entry.getValue())
            .sorted(Comparator.comparing(OrderData::getEntryDate))
            .limit(20)
            .map(order -> order.getItems().stream()
                    .map(item -> dataRoot.getStocks().get(item.getSupplyingWarehouseRef().getId()+item.getProductRef().getId()))
                    .collect(Collectors.toList()))
            .flatMap(products -> products.stream())
            .distinct()
            .collect(Collectors.toList());

    // synchronized access to read the stock infos
    int lowStockCount = this.consistencyManager.countStockEntriesLowerThanThreshold(stocksInOrder, req.getStockThreshold());
    return new StockLevelResponse(req, lowStockCount);
  }
}
