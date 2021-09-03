package de.uniba.dsg.jpb.service.ms;

import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.jpb.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.jpb.service.DeliveryService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.jacis.container.JacisContainer;
import org.jacis.store.JacisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsDeliveryService extends DeliveryService {

  private final JacisContainer container;
  private final JacisStore<String, WarehouseData> warehouseStore;
  private final JacisStore<String, DistrictData> districtStore;
  private final JacisStore<String, CustomerData> customerStore;
  private final JacisStore<String, OrderData> orderStore;
  private final JacisStore<String, OrderItemData> orderItemStore;
  private final JacisStore<String, CarrierData> carrierStore;

  @Autowired
  public MsDeliveryService(
      JacisContainer container,
      JacisStore<String, WarehouseData> warehouseStore,
      JacisStore<String, DistrictData> districtStore,
      JacisStore<String, CustomerData> customerStore,
      JacisStore<String, OrderData> orderStore,
      JacisStore<String, OrderItemData> orderItemStore,
      JacisStore<String, CarrierData> carrierStore) {
    this.container = container;
    this.warehouseStore = warehouseStore;
    this.districtStore = districtStore;
    this.customerStore = customerStore;
    this.orderStore = orderStore;
    this.orderItemStore = orderItemStore;
    this.carrierStore = carrierStore;
  }

  @Override
  public DeliveryResponse process(DeliveryRequest req) {
    return container.withLocalTx(
        () -> {
          // Find warehouse and carrier to be employed for delivery
          WarehouseData warehouse = warehouseStore.getReadOnly(req.getWarehouseId());
          List<DistrictData> districts =
              districtStore
                  .streamReadOnly(d -> d.getWarehouseId().equals(warehouse.getId()))
                  .collect(Collectors.toList());
          CarrierData carrier = carrierStore.getReadOnly(req.getCarrierId());

          // Attempt to deliver order from each district
          for (DistrictData district : districts) {
            double amountSum = 0;
            // Find oldest new/unfulfilled order

            OrderData order =
                orderStore.stream(
                        o -> o.getDistrictId().equals(district.getId()) && !o.isFulfilled())
                    .min(Comparator.comparing(OrderData::getEntryDate))
                    .orElse(null);
            if (order == null) {
              // No unfulfilled orders for this district, do nothing
              continue;
            }

            // Update fulfillment status and carrier of order
            order.setCarrierId(carrier.getId());
            order.setFulfilled(true);
            orderStore.update(order.getId(), order);

            // For each order item, set delivery date to now and sum amount
            List<OrderItemData> orderItems =
                orderItemStore.stream(i -> i.getOrderId().equals(order.getId()))
                    .collect(Collectors.toList());
            if (orderItems.isEmpty()) {
              throw new IllegalStateException("Order has no items");
            }
            for (OrderItemData orderItem : orderItems) {
              orderItem.setDeliveryDate(LocalDateTime.now());
              amountSum += orderItem.getAmount();
              orderItemStore.update(orderItem.getId(), orderItem);
            }

            // Update customer balance and delivery count
            CustomerData customer =
                customerStore.stream(c -> c.getId().equals(order.getCustomerId()))
                    .findAny()
                    .orElseThrow(
                        () -> {
                          return new IllegalStateException();
                        });
            customer.setBalance(customer.getBalance() + amountSum);
            customer.setDeliveryCount(customer.getDeliveryCount() + 1);
            customerStore.update(customer.getId(), customer);
          }
          return new DeliveryResponse(req);
        });
  }
}
