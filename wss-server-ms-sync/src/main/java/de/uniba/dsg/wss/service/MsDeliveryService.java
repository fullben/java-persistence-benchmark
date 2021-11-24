package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.access.DataConsistencyManager;
import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the transaction to be executed by the {@link DeliveryService} implementation.
 *
 * @author Johannes Manner
 * @author Benedikt Full
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
    List<OrderData> oldestOrderForEachDistrict =
        warehouse.getDistricts().values().stream()
            .map(
                districtData ->
                    districtData.getOrders().values().stream()
                        .filter(OrderData::isNotFulfilled)
                        .sorted()
                        .findFirst())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

    // update fulfillment status
    // update carrier information
    // for each order item, set delivery date to now and sum amount
    // Update customer balance and delivery count
    this.consistencyManager.deliverOldestOrders(oldestOrderForEachDistrict, carrier);

    return new DeliveryResponse(req);
  }
}
