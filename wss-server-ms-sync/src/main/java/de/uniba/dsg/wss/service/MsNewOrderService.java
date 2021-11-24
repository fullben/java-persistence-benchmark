package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.access.DataConsistencyManager;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.OrderItemData;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponseItem;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the transaction to be executed by the {@link NewOrderService} implementation.
 *
 * @author Johannes Manner
 * @author Benedikt Full
 */
@Service
public class MsNewOrderService extends NewOrderService {

  public static int maxRetries = 5;
  private static final Logger LOG = LogManager.getLogger(MsNewOrderService.class);
  private final DataConsistencyManager consistencyManager;
  private final MsDataRoot dataRoot;

  @Autowired
  public MsNewOrderService(DataConsistencyManager consistencyManager, MsDataRoot dataRoot) {
    this.consistencyManager = consistencyManager;
    this.dataRoot = dataRoot;
  }

  @Override
  public NewOrderResponse process(NewOrderRequest req) throws MsTransactionException {
    OrderData storedOrder = null;
    for (int i = 0; i < maxRetries; i++) {
      try {
        // synchronized access
        storedOrder = this.processOrderRequest(req);
        break;
      } catch (MsTransactionException e) {
        // TODO handle exception?
      }
    }

    // method stack memory
    return getNewOrderResponse(req, storedOrder);
  }

  private NewOrderResponse getNewOrderResponse(NewOrderRequest req, OrderData storedOrder) {
    if (storedOrder == null) {
      LOG.info("Cancel order processing - retries exceeded");
      throw new MsTransactionException("Can't process order");
    }

    // create return dtos
    double orderItemSum = 0;
    List<NewOrderResponseItem> dtoItems = new ArrayList<>();

    for (OrderItemData orderItem : storedOrder.getItems()) {
      // add to response object
      dtoItems.add(
          new NewOrderResponseItem(
              orderItem.getSupplyingWarehouseRef().getId(),
              orderItem.getProductRef().getId(),
              orderItem.getProductRef().getName(),
              orderItem.getProductRef().getPrice(),
              orderItem.getAmount(),
              orderItem.getQuantity(),
              orderItem.getLeftQuantityInStock(),
              determineBrandGeneric(orderItem.getProductRef().getData(), "stock data")));
      orderItemSum += orderItem.getAmount();
    }

    // prepare response object
    NewOrderResponse res =
        newOrderResponse(
            req,
            storedOrder.getId(),
            storedOrder.getEntryDate(),
            storedOrder.getDistrictRef().getWarehouse().getSalesTax(),
            storedOrder.getDistrictRef().getSalesTax(),
            storedOrder.getCustomerRef().getCredit(),
            storedOrder.getCustomerRef().getDiscount(),
            storedOrder.getCustomerRef().getLastName());
    res.setTotalAmount(
        calcOrderTotal(
            orderItemSum,
            storedOrder.getCustomerRef().getDiscount(),
            storedOrder.getDistrictRef().getWarehouse().getSalesTax(),
            storedOrder.getDistrictRef().getSalesTax()));
    res.setOrderItems(dtoItems);
    return res;
  }

  private OrderData processOrderRequest(NewOrderRequest req) throws MsTransactionException {
    // get basic data for transaction
    WarehouseData warehouseData = dataRoot.getWarehouses().get(req.getWarehouseId());
    CustomerData customerData = dataRoot.getCustomers().get(req.getCustomerId());
    if (warehouseData == null || customerData == null) {
      throw new IllegalArgumentException();
    }
    DistrictData districtData = warehouseData.getDistricts().get(req.getDistrictId());
    if (districtData == null) {
      throw new IllegalArgumentException();
    }

    // Get all supplying warehouses and products to ensure no invalid ids have been provided
    // optimization: checking if stock is available for warehouse and product
    List<StockUpdateDto> stockUpdates = new ArrayList<>();
    // Checks if all products come from the requested warehouse
    boolean allLocal = true;
    for (NewOrderRequestItem item : req.getItems()) {
      StockData stock =
          dataRoot.getStocks().get(item.getSupplyingWarehouseId() + item.getProductId());
      if (stock == null) {
        throw new IllegalArgumentException();
      }
      if (!req.getWarehouseId().equals(stock.getWarehouseRef().getId())) {
        allLocal = false;
      }
      StockUpdateDto stockUpdate = new StockUpdateDto(stock, item.getQuantity());
      stockUpdates.add(stockUpdate);
    }

    // create order
    OrderData order =
        new OrderData(
            districtData, customerData, LocalDateTime.now(), stockUpdates.size(), allLocal);

    return this.consistencyManager.storeOrder(order, stockUpdates);
  }

  private String getRandomDistrictInfo(StockData stock) {
    return randomDistrictData(
        List.of(
            stock.getDist01(),
            stock.getDist02(),
            stock.getDist03(),
            stock.getDist04(),
            stock.getDist05(),
            stock.getDist06(),
            stock.getDist07(),
            stock.getDist08(),
            stock.getDist09(),
            stock.getDist10()));
  }
}
