package de.uniba.dsg.jpb.clients.transaction;

import de.uniba.dsg.jpb.util.Stopwatch;
import de.uniba.dsg.jpb.messages.OrderRequest;
import de.uniba.dsg.jpb.messages.OrderRequestLine;
import de.uniba.dsg.jpb.messages.OrderResponse;
import de.uniba.dsg.jpb.util.NonUniformRandom;
import de.uniba.dsg.jpb.util.UniformRandom;
import java.util.ArrayList;
import java.util.List;

public class NewOrderTransaction {

  private static final UniformRandom USER_DATA_ENTRY_ERROR_RANDOM = new UniformRandom(1, 100);
  private static final UniformRandom SUPPLYING_WAREHOUSE_RANDOM = new UniformRandom(1, 100);
  private static final NonUniformRandom ITEM_ID_RANDOM = new NonUniformRandom(1, 100_000, 8191);
  private static final UniformRandom ITEM_QUANTITY_RANDOM = new UniformRandom(1, 10);
  private final Long warehouseId;
  private final Long districtId;
  private final Long customerId;
  private final int orderItemCount;
  private final boolean entryError;
  private boolean began;
  private boolean complete;
  private final Stopwatch stopwatch;

  public NewOrderTransaction(Long warehouseId) {
    this.warehouseId = warehouseId;
    districtId = new UniformRandom(1, 10).nextLong();
    customerId = new NonUniformRandom(1, 3000, 1023).nextLong();
    orderItemCount = new UniformRandom(5, 15).nextInt();
    entryError = USER_DATA_ENTRY_ERROR_RANDOM.nextInt() == 1;
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
    req.setDistrictId((long) districtId);
    req.setCustomerId(customerId);
    List<OrderRequestLine> lines = new ArrayList<>(orderItemCount);
    for (int i = 0; i < orderItemCount; i++) {
      OrderRequestLine line = new OrderRequestLine();
      long itemId;
      if (entryError && i == orderItemCount - 1) {
        itemId = 100_001;
      } else {
        itemId = ITEM_ID_RANDOM.nextLong();
      }
      line.setItemId(itemId);
      // TODO select a random warehouse id
      line.setSupplyingWarehouseId(SUPPLYING_WAREHOUSE_RANDOM.nextInt() == 1 ? -1 : warehouseId);
      line.setQuantity(ITEM_QUANTITY_RANDOM.nextInt());
      lines.add(line);
    }
    req.setLines(lines);
    began = true;
    stopwatch.start();
    return req;
  }

  public void complete(OrderResponse res) {
    // TODO param might be unnecessary
    if (!began) {
      throw new IllegalStateException();
    }
    stopwatch.stop();
    // TODO handle response
    System.out.println(res);
    complete = true;
  }
}
