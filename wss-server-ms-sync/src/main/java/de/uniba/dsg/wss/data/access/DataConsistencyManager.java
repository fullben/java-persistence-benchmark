package de.uniba.dsg.wss.data.access;

import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.OrderItemData;
import de.uniba.dsg.wss.data.model.PaymentData;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.service.MsTransactionException;
import de.uniba.dsg.wss.service.StockUpdateDto;
import java.util.ArrayList;
import java.util.List;
import one.microstream.storage.types.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles all concurrent accesses from any of the services. All lock objects and their usage is
 * included here.
 *
 * @author Johannes Manner
 * @author Benedikt Full
 */
@Component
public class DataConsistencyManager {

  // low level synchronization here - one lock for updating the overall order
  private final Object stockLock;
  private final StorageManager storageManger;
  private final MsDataRoot dataRoot;

  @Autowired
  public DataConsistencyManager(StorageManager storageManager, MsDataRoot dataRoot) {
    stockLock = new Object();
    this.storageManger = storageManager;
    this.dataRoot = dataRoot;
  }

  private List<OrderItemData> updateStock(OrderData order, List<StockUpdateDto> stockUpdates) {
    synchronized (stockLock) {
      List<OrderItemData> orderItemList = new ArrayList<>();
      int i = 0;
      for (i = 0; i < stockUpdates.size(); i++) {
        // update all the items, if an update fails, compensate the changes
        StockUpdateDto stockUpdate = stockUpdates.get(i);
        if (!stockUpdate.getStockData().reduceQuantity(stockUpdate.getQuantity())) {
          break;
        } else {
          OrderItemData orderItem =
              new OrderItemData(
                  order,
                  stockUpdate.getStockData().getProductRef(),
                  stockUpdate.getStockData().getWarehouseRef(),
                  i,
                  stockUpdate.getQuantity(),
                  stockUpdate.getStockData().getQuantity(),
                  stockUpdate.getQuantity() * stockUpdate.getStockData().getProductRef().getPrice(),
                  stockUpdate.getStockData().getDist01());
          orderItemList.add(orderItem);
        }
      }

      // compensate the first transactions, if some updates fail
      if (i != stockUpdates.size()) {
        for (int j = 0; j < i; j++) {
          StockUpdateDto stockUpdate = stockUpdates.get(j);
          stockUpdate.getStockData().undoReduceQuantityOperation(stockUpdate.getQuantity());
        }
        return List.of();
      }
      return orderItemList;
    }
  }

  public OrderData storeOrder(OrderData order, List<StockUpdateDto> stockUpdates)
      throws MsTransactionException {
    synchronized (storageManger) {
      List<OrderItemData> itemList = updateStock(order, stockUpdates);
      if (itemList.isEmpty()) {
        throw new MsTransactionException("Order item update failed");
      }
      order.getItems().addAll(itemList);

      dataRoot.getOrders().put(order.getId(), order);

      // referential integrity (customer and district)
      order.getDistrictRef().getOrders().put(order.getId(), order);
      order.getCustomerRef().getOrderRefs().put(order.getId(), order);

      // A single store is faster as making a store for each object separately
      storageManger.storeRoot();
      return order;
    }
  }

  public CustomerData storePaymentAndUpdateDependentObjects(
      WarehouseData warehouseData,
      DistrictData districtData,
      CustomerData customer,
      PaymentData payment) {
    double amount = payment.getAmount();
    synchronized (storageManger) {
      // update warehouse - increase year to balance
      warehouseData.increaseYearToBalance(amount);
      // update district - increase year to balance
      districtData.increaseYearToBalance(amount);
      // optimization acquiring the lock here
      CustomerData copiedCustomer;
      synchronized (customer.getId()) {
        // add payment to customer
        customer.getPaymentRefs().add(payment);
        // update customer - decrease balance - req.amount
        customer.decreaseBalance(amount);
        // update customer - increase year to date balance + req.amount
        customer.increaseYearToBalance(amount);
        // update customer - update payment count + 1
        customer.increasePaymentCount();
        // update customer if he/she has bad credit
        if (customerHasBadCredit(customer.getCredit())) {
          customer.updateData(
              buildNewCustomerData(
                  customer.getId(),
                  warehouseData.getId(),
                  districtData.getId(),
                  amount,
                  customer.getData()));
        }
        // should be done within the synchronized block
        copiedCustomer = new CustomerData(customer);
      }

      storageManger.storeRoot();
      // only limited copy
      return copiedCustomer;
    }
  }

  public void deliverOldestOrders(List<OrderData> oldestOrderForEachDistrict, CarrierData carrier) {
    synchronized (storageManger) {
      for (OrderData order : oldestOrderForEachDistrict) {
        // in cases where two terminal worker update the same oldestOrders in parallel,
        // and they are both competing for the storageManager lock
        if (order.isFulfilled()) {
          continue;
        }
        CustomerData customer = order.getCustomerRef();
        synchronized (customer.getId()) {
          synchronized (order.getId()) {
            // update carrier information
            order.updateCarrier(carrier);
            // update fulfillment status
            order.setAsFulfilled();
          }
          // compute amount of order
          double amount = 0;
          for (OrderItemData itemData : order.getItems()) {
            itemData.updateDeliveryDate();
            amount += itemData.getAmount();
          }
          customer.increaseBalance(amount);
          customer.increaseDeliveryCount();
        }
      }
      storageManger.storeRoot();
    }
  }

  // COPIED FROM PaymentService
  protected boolean customerHasBadCredit(String customerCredit) {
    return "BC".equals(customerCredit);
  }

  // COPIED FROM PaymentService
  protected String buildNewCustomerData(
      String customerId, String warehouseId, String districtId, double amount, String oldData) {
    String newData = "" + customerId + districtId + warehouseId + amount;
    return newData + oldData.substring(newData.length());
  }

  public int countStockEntriesLowerThanThreshold(List<StockData> stocks, int stockThreshold) {
    synchronized (stockLock) {
      return (int) stocks.parallelStream().filter(s -> s.getQuantity() < stockThreshold).count();
    }
  }
}
