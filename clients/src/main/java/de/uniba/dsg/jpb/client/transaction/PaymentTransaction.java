package de.uniba.dsg.jpb.client.transaction;

import de.uniba.dsg.jpb.messages.PaymentRequest;
import de.uniba.dsg.jpb.messages.PaymentResponse;
import de.uniba.dsg.jpb.util.RandomSelector;
import de.uniba.dsg.jpb.util.Stopwatch;
import de.uniba.dsg.jpb.util.UniformRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaymentTransaction {

  private static final Logger LOG = LogManager.getLogger(PaymentTransaction.class);
  private static final UniformRandom AMOUNT_RANDOM = new UniformRandom(1.0, 5.000, 2);
  private final Long warehouseId;
  private final Map<Long, List<Long>> customerIdsByDistrictId;
  private final RandomSelector<Long> districtIdSelector;
  private final Stopwatch stopwatch;
  private boolean began;
  private boolean complete;

  public PaymentTransaction(Long warehouseId, Map<Long, List<Long>> customerIdsByDistrictId) {
    this.warehouseId = warehouseId;
    this.customerIdsByDistrictId = customerIdsByDistrictId;
    this.districtIdSelector =
        new RandomSelector<>(new ArrayList<>(customerIdsByDistrictId.keySet()));
    stopwatch = new Stopwatch();
    began = false;
    complete = false;
  }

  public PaymentRequest begin() {
    began = true;
    stopwatch.start();
    PaymentRequest req = new PaymentRequest();
    req.setWarehouseId(warehouseId);
    req.setDistrictId(districtIdSelector.next());
    req.setCustomerId(
        new RandomSelector<>(customerIdsByDistrictId.get(req.getDistrictId())).next());
    req.setAmount(AMOUNT_RANDOM.nextDouble());
    return req;
  }

  public void complete(PaymentResponse res) {
    if (!began || complete) {
      throw new IllegalStateException();
    }
    stopwatch.stop();
    LOG.info(
        "Successfully completed a {}, took {} ms",
        PaymentTransaction.class.getSimpleName(),
        stopwatch.getDurationMillis());
    complete = true;
  }
}
