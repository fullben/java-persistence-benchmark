package de.uniba.dsg.jpb.service.jpa;

import de.uniba.dsg.jpb.data.access.jpa.OrderRepository;
import de.uniba.dsg.jpb.data.access.jpa.StockRepository;
import de.uniba.dsg.jpb.data.model.jpa.OrderEntity;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.jpb.service.StockLevelService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaStockLevelService extends StockLevelService {

  private final OrderRepository orderRepository;
  private final StockRepository stockRepository;

  @Autowired
  public JpaStockLevelService(OrderRepository orderRepository, StockRepository stockRepository) {
    this.orderRepository = orderRepository;
    this.stockRepository = stockRepository;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  @Override
  public StockLevelResponse process(StockLevelRequest req) {
    // Find the most 20 recent orders for the district
    List<OrderEntity> orders =
        orderRepository.find20MostRecentOrdersOfDistrict(req.getDistrictId());

    // Find the corresponding stock objects and count the ones below the given threshold
    List<String> productIds =
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
