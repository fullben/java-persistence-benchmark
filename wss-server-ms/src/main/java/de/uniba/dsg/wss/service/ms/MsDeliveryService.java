package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.access.ms.DataConsistencyManager;
import de.uniba.dsg.wss.data.model.ms.CarrierData;
import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import de.uniba.dsg.wss.data.model.ms.OrderData;
import de.uniba.dsg.wss.data.model.ms.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implements the mentioned transactions in the README.
 *
 * @author Johannes Manner
 */
@Service
public class MsDeliveryService extends DeliveryService {

  private final DataConsistencyManager consistencyManager;
  private final MsDataRoot dataRoot;

  @Autowired
  public MsDeliveryService(DataConsistencyManager consistencyManager, MsDataRoot dataRoot) {
    this.consistencyManager = consistencyManager;
    this.dataRoot = dataRoot;
  }

  @Override
  public DeliveryResponse process(DeliveryRequest req) {

    WarehouseData warehouse = this.dataRoot.getWarehouses().get(req.getWarehouseId());
    CarrierData carrier = this.dataRoot.getCarriers().get(req.getCarrierId());

    // Find an order for each district (the oldest unfulfilled order)
    List<OrderData> oldestOrderForEachDistrict = warehouse.getDistricts().entrySet().stream()
            .map(dEntry -> dEntry.getValue().getOrders().entrySet().stream()
                    .map(oEntry -> oEntry.getValue())
                    .filter(OrderData::isNotFulfilled)
                    .sorted()
                    .findFirst())
            .filter(Optional::isPresent)
            .map(o -> o.get())
            .collect(Collectors.toList());

    // update fulfillment status
    // update carrier information
    // for each order item, set delivery date to now and sum amount
    // Update customer balance and delivery count
    this.consistencyManager.deliverOldestOrders(oldestOrderForEachDistrict, carrier);

    return new DeliveryResponse(req);
  }
}
