package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.access.ms.TransactionManager;
import de.uniba.dsg.wss.data.model.ms.CarrierData;
import de.uniba.dsg.wss.data.model.ms.CustomerData;
import de.uniba.dsg.wss.data.model.ms.DistrictData;
import de.uniba.dsg.wss.data.model.ms.OrderData;
import de.uniba.dsg.wss.data.model.ms.OrderItemData;
import de.uniba.dsg.wss.data.model.ms.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.service.DeliveryService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    TransactionManager transactionManager = new TransactionManager(container).setAttempts(3);
    return transactionManager.commit(
        () -> {
          // Find warehouse and carrier to be employed for delivery
          WarehouseData warehouse = warehouseStore.getReadOnly(req.getWarehouseId());
          List<DistrictData> districts =
              districtStore
                  .streamReadOnly(d -> d.getWarehouseId().equals(warehouse.getId()))
                  .parallel()
                  .collect(Collectors.toList());
          CarrierData carrier = carrierStore.getReadOnly(req.getCarrierId());

          // Find an order for each district (the oldest unfulfilled order)
          List<String> orderIds =
              districts.parallelStream()
                  .map(
                      d ->
                          orderStore
                              .streamReadOnly(
                                  o -> o.getDistrictId().equals(d.getId()) && !o.isFulfilled())
                              .parallel()
                              .min(Comparator.comparing(OrderData::getEntryDate))
                              .orElse(null))
                  .filter(Objects::nonNull)
                  .map(OrderData::getId)
                  .collect(Collectors.toList());
          List<OrderData> orders =
              orderIds.stream().map(orderStore::get).collect(Collectors.toList());

          // Get the order items of all orders
          List<OrderItemData> allOrderItems =
              orderItemStore.stream(i -> orderIds.contains(i.getOrderId()))
                  .parallel()
                  .collect(Collectors.toList());

          // Actually deliver the orders
          for (OrderData order : orders) {
            double amountSum = 0;
            // Update fulfillment status and carrier of order
            order.setCarrierId(carrier.getId());
            order.setFulfilled(true);
            orderStore.update(order.getId(), order);

            // For each order item, set delivery date to now and sum amount
            List<OrderItemData> orderItems =
                allOrderItems.stream()
                    .filter(i -> i.getOrderId().equals(order.getId()))
                    .peek(i -> i.setDeliveryDate(LocalDateTime.now()))
                    .collect(Collectors.toList());
            if (orderItems.isEmpty()) {
              throw new IllegalStateException("Order has no items");
            }
            amountSum += orderItems.stream().mapToDouble(OrderItemData::getAmount).sum();

            // Update customer balance and delivery count
            CustomerData customer = customerStore.get(order.getCustomerId());
            customer.setBalance(customer.getBalance() + amountSum);
            customer.setDeliveryCount(customer.getDeliveryCount() + 1);
            customerStore.update(customer.getId(), customer);
          }
          orderItemStore.update(allOrderItems, OrderItemData::getId);

          return new DeliveryResponse(req);
        });
  }
}
