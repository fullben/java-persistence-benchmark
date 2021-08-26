package de.uniba.dsg.jpb.service.ms;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.Find;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsDeliveryService extends DeliveryService {

  private final DataManager dataManager;

  public MsDeliveryService(DataManager dataManager) {
    this.dataManager = dataManager;
  }

  @Override
  public DeliveryResponse process(DeliveryRequest req) {
    return dataManager.write(
        (root, storageManager) -> {
          // Find warehouse and carrier to be employed for delivery
          WarehouseData warehouse =
              Find.warehouseById(req.getWarehouseId(), root.findAllWarehouses());
          CarrierData carrier = Find.carrierById(req.getCarrierId(), root.findAllCarriers());

          List<OrderData> editedOrders = new ArrayList<>();
          List<CustomerCache> editedCustomers = new ArrayList<>();
          // Attempt to deliver order from each district
          for (DistrictData district : warehouse.getDistricts()) {
            double amountSum = 0;
            // Find oldest new/unfulfilled order
            OrderData order = Find.oldestUnfulfilledOrderOfDistrict(district).orElse(null);
            if (order == null) {
              // No unfulfilled orders for this district, do nothing
              continue;
            }

            // Update fulfillment status and carrier of order
            order.setCarrier(carrier);
            order.setFulfilled(true);

            // For each order item, set delivery date to now and sum amount
            for (OrderItemData orderItem : order.getItems()) {
              orderItem.setDeliveryDate(LocalDateTime.now());
              amountSum += orderItem.getAmount();
            }

            // Update customer balance and delivery count
            CustomerData customer = order.getCustomer();
            final double customerBalance = customer.getBalance();
            customer.setBalance(customerBalance + amountSum);
            final int customerDeliveryCount = customer.getDeliveryCount();
            customer.setDeliveryCount(customerDeliveryCount + 1);

            editedOrders.add(order);
            editedCustomers.add(
                new CustomerCache(customer, customerBalance, customerDeliveryCount));
          }

          try {
            // Persist the changes
            List<Object> toBeStored = new ArrayList<>(editedOrders.size() + editedCustomers.size());
            toBeStored.addAll(editedOrders);
            toBeStored.addAll(
                editedCustomers.stream().map(c -> c.customer).collect(Collectors.toList()));
            storageManager.storeAll(toBeStored);
          } catch (RuntimeException e) {
            // Rollback in case of a runtime exception
            editedOrders.forEach(
                o -> {
                  o.setCarrier(null);
                  o.setFulfilled(false);
                  for (OrderItemData orderItem : o.getItems()) {
                    orderItem.setDeliveryDate(null);
                  }
                });
            editedCustomers.forEach(
                c -> {
                  c.customer.setBalance(c.balance);
                  c.customer.setDeliveryCount(c.deliveryCount);
                });
            throw e;
          }
          return new DeliveryResponse(req);
        });
  }

  private static class CustomerCache {

    private final CustomerData customer;
    private final double balance;
    private final int deliveryCount;

    private CustomerCache(CustomerData customer, double balance, int deliveryCount) {
      this.customer = customer;
      this.balance = balance;
      this.deliveryCount = deliveryCount;
    }
  }
}
