package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.access.DataConsistencyManager;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the transaction to be executed by the {@link StockLevelService} implementation.
 *
 * @author Johannes Manner
 * @author Benedikt Full
 */
@Service
public class MsStockLevelService extends StockLevelService {

  private final MsDataRoot dataRoot;
  private final DataConsistencyManager consistencyManager;

  @Autowired
  public MsStockLevelService(MsDataRoot dataRoot, DataConsistencyManager consistencyManager) {
    this.dataRoot = dataRoot;
    this.consistencyManager = consistencyManager;
  }

  @Override
  public StockLevelResponse process(StockLevelRequest req) {
    WarehouseData warehouse = this.dataRoot.getWarehouses().get(req.getWarehouseId());
    DistrictData district = warehouse.getDistricts().get(req.getDistrictId());
    List<StockData> stocksInOrder =
        district.getOrders().values().stream()
            .sorted(Comparator.comparing(OrderData::getEntryDate))
            .limit(20)
            .map(
                order ->
                    order.getItems().stream()
                        .map(
                            item ->
                                dataRoot
                                    .getStocks()
                                    .get(
                                        item.getSupplyingWarehouseRef().getId()
                                            + item.getProductRef().getId()))
                        .collect(Collectors.toList()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());

    // synchronized access to read the stock infos
    int lowStockCount =
        this.consistencyManager.countStockEntriesLowerThanThreshold(
            stocksInOrder, req.getStockThreshold());
    return new StockLevelResponse(req, lowStockCount);
  }
}
