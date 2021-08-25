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
import one.microstream.persistence.types.Storer;
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
            customer.setDeliveryCount(customer.getDeliveryCount() + 1);

            try {
              // Persist the changes
              Storer storer = storageManager.createEagerStorer();
              storer.storeAll(order, customer);
              storer.commit();
            } catch (RuntimeException e) {
              // Rollback in case of a runtime exception
              order.setCarrier(null);
              order.setFulfilled(false);
              for (OrderItemData orderItem : order.getItems()) {
                orderItem.setDeliveryDate(null);
              }
              customer.setBalance(customerBalance);
              customer.setDeliveryCount(customer.getDeliveryCount() - 1);
              throw e;
            }
          }
          return new DeliveryResponse(req);
        });
  }
}
