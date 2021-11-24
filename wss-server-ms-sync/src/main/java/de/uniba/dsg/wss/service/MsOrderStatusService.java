package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.transfer.messages.OrderItemStatusResponse;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the transaction to be executed by the {@link OrderStatusService} implementation.
 *
 * @author Johannes Manner
 * @author Benedikt Full
 */
@Service
public class MsOrderStatusService extends OrderStatusService {

  private final MsDataRoot dataRoot;

  @Autowired
  public MsOrderStatusService(MsDataRoot dataRoot) {
    this.dataRoot = dataRoot;
  }

  @Override
  public OrderStatusResponse process(OrderStatusRequest req) {

    CustomerData customer;
    if (req.getCustomerId() == null) {
      customer =
          dataRoot.getCustomers().entrySet().stream()
              .parallel()
              .filter(c -> c.getValue().getEmail().equals(req.getCustomerEmail()))
              .findAny()
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "Failed to find customer with email " + req.getCustomerEmail()))
              .getValue();
    } else {
      customer = dataRoot.getCustomers().get(req.getCustomerId());
      if (customer == null) {
        throw new IllegalStateException(
            "Failed to find customer with email " + req.getCustomerEmail());
      }
    }

    OrderData mostRecentOrder =
        customer.getOrderRefs().values().stream()
            .max(Comparator.comparing(OrderData::getEntryDate))
            .orElseThrow(IllegalStateException::new);

    // synchronize the read access here, since the carrier for example could be set in the meantime
    // to another value
    synchronized (customer.getId()) {
      synchronized (mostRecentOrder.getId()) {
        return new OrderStatusResponse(
            mostRecentOrder.getDistrictRef().getWarehouse().getId(),
            mostRecentOrder.getDistrictRef().getId(),
            customer.getId(),
            customer.getFirstName(),
            customer.getMiddleName(),
            customer.getLastName(),
            customer.getBalance(),
            mostRecentOrder.getId(),
            mostRecentOrder.getEntryDate(),
            mostRecentOrder.getCarrierRef() == null
                ? null
                : mostRecentOrder.getCarrierRef().getId(),
            mostRecentOrder.getItems().stream()
                .map(
                    item ->
                        new OrderItemStatusResponse(
                            item.getSupplyingWarehouseRef().getId(),
                            item.getProductRef().getId(),
                            item.getQuantity(),
                            item.getAmount(),
                            item.getDeliveryDate()))
                .collect(Collectors.toList()));
      }
    }
  }
}
