package de.uniba.dsg.wss.ms.service;

import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponseItem;
import de.uniba.dsg.wss.ms.data.access.TransactionManager;
import de.uniba.dsg.wss.ms.data.model.CustomerData;
import de.uniba.dsg.wss.ms.data.model.DistrictData;
import de.uniba.dsg.wss.ms.data.model.OrderData;
import de.uniba.dsg.wss.ms.data.model.OrderItemData;
import de.uniba.dsg.wss.ms.data.model.ProductData;
import de.uniba.dsg.wss.ms.data.model.StockData;
import de.uniba.dsg.wss.ms.data.model.WarehouseData;
import de.uniba.dsg.wss.service.NewOrderService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jacis.container.JacisContainer;
import org.jacis.store.JacisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MsNewOrderService extends NewOrderService {

  private final JacisContainer container;
  private final JacisStore<String, WarehouseData> warehouseStore;
  private final JacisStore<String, DistrictData> districtStore;
  private final JacisStore<String, StockData> stockStore;
  private final JacisStore<String, CustomerData> customerStore;
  private final JacisStore<String, OrderData> orderStore;
  private final JacisStore<String, OrderItemData> orderItemStore;
  private final JacisStore<String, ProductData> productStore;

  @Autowired
  public MsNewOrderService(
      JacisContainer container,
      JacisStore<String, WarehouseData> warehouseStore,
      JacisStore<String, DistrictData> districtStore,
      JacisStore<String, StockData> stockStore,
      JacisStore<String, CustomerData> customerStore,
      JacisStore<String, OrderData> orderStore,
      JacisStore<String, OrderItemData> orderItemStore,
      JacisStore<String, ProductData> productStore) {
    this.container = container;
    this.warehouseStore = warehouseStore;
    this.districtStore = districtStore;
    this.stockStore = stockStore;
    this.customerStore = customerStore;
    this.orderStore = orderStore;
    this.orderItemStore = orderItemStore;
    this.productStore = productStore;
  }

  @Override
  public NewOrderResponse process(NewOrderRequest req) {
    TransactionManager transactionManager = new TransactionManager(container, 5, 100);
    return transactionManager.commit(
        () -> {
          // Get warehouse, district and customer
          WarehouseData warehouse = warehouseStore.getReadOnly(req.getWarehouseId());
          DistrictData district = districtStore.getReadOnly(req.getDistrictId());
          if (!district.getId().equals(req.getDistrictId())
              || !district.getWarehouseId().equals(req.getWarehouseId())) {
            throw new IllegalArgumentException();
          }
          CustomerData customer = customerStore.get(req.getCustomerId());

          // Get all supplying warehouses and products to ensure no invalid ids have been
          // provided
          List<String> supplyingWarehouseIds =
              req.getItems().stream()
                  .map(NewOrderRequestItem::getSupplyingWarehouseId)
                  .collect(Collectors.toList());
          List<String> productIds =
              req.getItems().stream()
                  .map(NewOrderRequestItem::getProductId)
                  .collect(Collectors.toList());

          List<WarehouseData> supplyingWarehouses =
              supplyingWarehouseIds.stream()
                  .map(warehouseStore::getReadOnly)
                  .collect(Collectors.toList());
          List<ProductData> orderItemProducts =
              productIds.stream().map(productStore::getReadOnly).collect(Collectors.toList());

          // Get all relevant stocks
          List<StockData> stocks =
              stockStore.stream(
                      s ->
                          productIds.contains(s.getProductId())
                              && supplyingWarehouseIds.contains(s.getWarehouseId()))
                  .parallel()
                  .collect(Collectors.toList());

          // Create a new order
          OrderData order = new OrderData();
          order.setDistrictId(district.getId());
          order.setCustomerId(customer.getId());
          order.setEntryDate(LocalDateTime.now());
          order.setItemCount(req.getItems().size());
          orderStore.update(order.getId(), order);

          List<NewOrderResponseItem> responseLines = new ArrayList<>(req.getItems().size());
          double orderItemSum = 0;
          for (int i = 0; i < req.getItems().size(); i++) {
            NewOrderRequestItem reqItem = req.getItems().get(i);
            WarehouseData supplyingWarehouse =
                supplyingWarehouses.stream()
                    .filter(w -> w.getId().equals(reqItem.getSupplyingWarehouseId()))
                    .findAny()
                    .orElseThrow(
                        () ->
                            new IllegalStateException(
                                "Failed to find warehouse " + reqItem.getSupplyingWarehouseId()));
            ProductData product =
                orderItemProducts.stream()
                    .filter(p -> p.getId().equals(reqItem.getProductId()))
                    .findAny()
                    .orElseThrow(
                        () ->
                            new IllegalStateException(
                                "Failed to find product " + reqItem.getProductId()));
            StockData stock =
                stocks.stream()
                    .filter(
                        s ->
                            s.getWarehouseId().equals(supplyingWarehouse.getId())
                                && s.getProductId().equals(product.getId()))
                    .findAny()
                    .orElseThrow(
                        () ->
                            new IllegalStateException(
                                "Failed to find stock for product "
                                    + product.getId()
                                    + " of warehouse "
                                    + supplyingWarehouse.getId()));

            OrderItemData orderItem = new OrderItemData();
            orderItem.setOrderId(order.getId());
            orderItem.setNumber(i + 1);
            orderItem.setProductId(reqItem.getProductId());
            orderItem.setSupplyingWarehouseId(reqItem.getSupplyingWarehouseId());
            orderItem.setDeliveryDate(null);
            orderItem.setQuantity(reqItem.getQuantity());
            orderItem.setAmount(orderItemProducts.get(i).getPrice() * reqItem.getQuantity());
            orderItem.setDistInfo(getRandomDistrictInfo(stock));

            orderItemStore.update(orderItem.getId(), orderItem);

            NewOrderResponseItem responseLine = newOrderResponseLine(orderItem);
            responseLines.add(responseLine);
            int stockQuantity = stock.getQuantity();
            int orderItemQuantity = orderItem.getQuantity();
            stock.setQuantity(determineNewStockQuantity(stockQuantity, orderItemQuantity));
            stock.setYearToDateBalance(stock.getYearToDateBalance() + orderItemQuantity);
            stock.setOrderCount(stock.getOrderCount() + 1);
            stockStore.update(stock.getId(), stock);
            responseLine.setStockQuantity(stock.getQuantity());
            responseLine.setItemName(product.getName());
            responseLine.setItemPrice(product.getPrice());
            responseLine.setAmount(product.getPrice() * orderItemQuantity);
            responseLine.setBrandGeneric(determineBrandGeneric(product.getData(), stock.getData()));

            orderItemSum += orderItem.getAmount();
          }

          // Prepare the response object
          NewOrderResponse res =
              newOrderResponse(
                  req,
                  order.getId(),
                  order.getEntryDate(),
                  warehouse.getSalesTax(),
                  district.getSalesTax(),
                  customer.getCredit(),
                  customer.getDiscount(),
                  customer.getLastName());
          res.setOrderId(order.getId());
          res.setOrderTimestamp(order.getEntryDate());
          res.setTotalAmount(
              calcOrderTotal(
                  orderItemSum,
                  customer.getDiscount(),
                  warehouse.getSalesTax(),
                  district.getSalesTax()));
          res.setOrderItems(responseLines);

          return res;
        });
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

  private static NewOrderResponseItem newOrderResponseLine(OrderItemData item) {
    NewOrderResponseItem requestLine = new NewOrderResponseItem();
    requestLine.setSupplyingWarehouseId(item.getSupplyingWarehouseId());
    requestLine.setItemId(item.getProductId());
    requestLine.setItemPrice(0);
    requestLine.setAmount(item.getAmount());
    requestLine.setQuantity(item.getQuantity());
    requestLine.setStockQuantity(0);
    requestLine.setBrandGeneric(null);
    return requestLine;
  }
}
