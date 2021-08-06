package de.uniba.dsg.jpb.client.transaction;

import de.uniba.dsg.jpb.messages.OrderRequest;
import de.uniba.dsg.jpb.messages.OrderRequestItem;
import de.uniba.dsg.jpb.messages.OrderResponse;
import de.uniba.dsg.jpb.util.NonUniformRandom;
import de.uniba.dsg.jpb.util.RandomSelector;
import de.uniba.dsg.jpb.util.Stopwatch;
import de.uniba.dsg.jpb.util.UniformRandom;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewOrderTransaction {

  private static final Logger LOG = LogManager.getLogger(NewOrderTransaction.class);
  private static final UniformRandom ONE_TO_ONE_HUNDRED_RANDOM = new UniformRandom(1, 100);
  private static final UniformRandom ITEM_QUANTITY_RANDOM = new UniformRandom(1, 10);
  private final Long warehouseId;
  private final Long districtId;
  private final Long customerId;
  private final int orderItemCount;
  private final boolean entryError;
  private boolean began;
  private boolean complete;
  private final Stopwatch stopwatch;
  private RandomSelector<Long> itemIdSelector;
  private RandomSelector<Long> warehouseIdSelector;

  public NewOrderTransaction(
      List<Long> warehouseIds,
      Long warehouseId,
      Long districtId,
      Long customerId,
      List<Long> itemIds) {
    this.warehouseId = warehouseId;
    this.districtId = districtId;
    this.customerId = customerId;
    itemIdSelector = new RandomSelector<>(itemIds);
    warehouseIdSelector = new RandomSelector<>(warehouseIds);
    orderItemCount = new UniformRandom(5, 15).nextInt();
    entryError = isOneInAHundred();
    began = false;
    complete = false;
    stopwatch = new Stopwatch();
  }

  public NewOrderTransaction(Long warehouseId) {
    this.warehouseId = warehouseId;
    districtId = new UniformRandom(1, 10).nextLong();
    customerId = new NonUniformRandom(1, 3000, 1023).nextLong();
    orderItemCount = new UniformRandom(5, 15).nextInt();
    entryError = isOneInAHundred();
    began = false;
    complete = false;
    stopwatch = new Stopwatch();
  }

  public OrderRequest begin() {
    if (began || complete) {
      throw new IllegalStateException();
    }
    OrderRequest req = new OrderRequest();
    req.setWarehouseId(warehouseId);
    req.setDistrictId(districtId);
    req.setCustomerId(customerId);
    List<OrderRequestItem> lines = new ArrayList<>(orderItemCount);
    for (int i = 0; i < orderItemCount; i++) {
      OrderRequestItem line = new OrderRequestItem();
      long itemId;
      if (entryError && i == orderItemCount - 1) {
        itemId = 100_001;
      } else {
        itemId = itemIdSelector.next();
      }
      line.setItemId(itemId);
      line.setSupplyingWarehouseId(selectSupplyingWarehouseId());
      line.setQuantity(ITEM_QUANTITY_RANDOM.nextInt());
      lines.add(line);
    }
    req.setItems(lines);
    began = true;
    stopwatch.start();
    return req;
  }

  public void complete(OrderResponse res) {
    if (!began || complete) {
      throw new IllegalStateException();
    }
    stopwatch.stop();
    // TODO actually handle response?
    if (res.getMessage() != null) {
      LOG.info(
          "Error while completing a {}, order id is {}, took {} ms",
          NewOrderTransaction.class.getSimpleName(),
          res.getOrderId(),
          stopwatch.getDurationMillis());
    } else {
      LOG.info(
          "Successfully completed a {}, order id is {}, took {} ms",
          NewOrderTransaction.class.getSimpleName(),
          res.getOrderId(),
          stopwatch.getDurationMillis());
    }
    complete = true;
  }

  private long selectSupplyingWarehouseId() {
    long supplyingWarehouseId;
    if (isOneInAHundred()) {
      do {
        supplyingWarehouseId = warehouseIdSelector.next();
      } while (supplyingWarehouseId == warehouseId);
    } else {
      supplyingWarehouseId = warehouseId;
    }
    return supplyingWarehouseId;
  }

  private static boolean isOneInAHundred() {
    return ONE_TO_ONE_HUNDRED_RANDOM.nextInt() == 1;
  }
}
