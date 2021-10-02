package de.uniba.dsg.wss.service.jpa;

import de.uniba.dsg.wss.data.access.jpa.OrderRepository;
import de.uniba.dsg.wss.data.access.jpa.StockRepository;
import de.uniba.dsg.wss.data.model.jpa.OrderEntity;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.wss.service.StockLevelService;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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

  @Retryable(
      value = {RuntimeException.class, SQLException.class, PSQLException.class},
      backoff = @Backoff(delay = 100),
      maxAttempts = 5)
  @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
  @Override
  public StockLevelResponse process(StockLevelRequest req) {
    // Find the most 20 recent orders for the district
    List<OrderEntity> orders =
        orderRepository.findTwentyMostRecentOrdersOfDistrict(req.getDistrictId());

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
