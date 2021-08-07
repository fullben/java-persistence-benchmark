package de.uniba.dsg.jpb.server.service.jpa;

import de.uniba.dsg.jpb.data.model.jpa.OrderEntity;
import de.uniba.dsg.jpb.messages.StockLevelRequest;
import de.uniba.dsg.jpb.messages.StockLevelResponse;
import de.uniba.dsg.jpb.server.data.access.jpa.OrderRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.StockRepository;
import de.uniba.dsg.jpb.server.service.StockLevelService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaStockLevelService extends StockLevelService {

  private final OrderRepository orderRepository;
  private final StockRepository stockRepository;

  @Autowired
  public JpaStockLevelService(OrderRepository orderRepository, StockRepository stockRepository) {
    this.orderRepository = orderRepository;
    this.stockRepository = stockRepository;
  }

  @Transactional
  @Override
  public StockLevelResponse process(StockLevelRequest req) {
    // Find the most 20 recent orders for the district
    List<OrderEntity> orders =
        orderRepository.find20MostRecentOrdersOfDistrict(req.getDistrictId());
    // Find the corresponding stock objects and count the ones below the given threshold
    List<Long> productIds =
        orders.stream()
            .flatMap(o -> o.getItems().stream().map(i -> i.getProduct().getId()))
            .distinct()
            .collect(Collectors.toList());
    int lowStockCount =
        stockRepository
            .findByWarehouseIdAndProductIdAndQuantityThreshold(
                req.getWarehouseId(), productIds, req.getStockThreshold())
            .size();
    StockLevelResponse res = new StockLevelResponse(req);
    res.setLowStocksCount(lowStockCount);
    return res;
  }
}
