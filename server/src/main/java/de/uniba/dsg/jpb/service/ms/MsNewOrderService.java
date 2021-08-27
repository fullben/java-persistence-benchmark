package de.uniba.dsg.jpb.service.ms;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.DataNotFoundException;
import de.uniba.dsg.jpb.data.access.ms.Find;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.StockData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderResponseItem;
import de.uniba.dsg.jpb.service.NewOrderService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import one.microstream.persistence.types.Storer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsNewOrderService extends NewOrderService {

  private final DataManager dataManager;

  public MsNewOrderService(DataManager dataManager) {
    this.dataManager = dataManager;
  }

  @Override
  public NewOrderResponse process(NewOrderRequest req) {
    return dataManager.write(
        (root, storageManager) -> {
          // Get warehouse, district and customer
          WarehouseData warehouse =
              Find.warehouseById(req.getWarehouseId(), root.findAllWarehouses());
          DistrictData district = Find.districtById(req.getDistrictId(), warehouse);
          if (!district.getId().equals(req.getDistrictId())
              || !district.getWarehouse().getId().equals(req.getWarehouseId())) {
            throw new IllegalArgumentException();
          }
          CustomerData customer = Find.customerById(req.getCustomerId(), district);

          // Get all supplying warehouses and products to ensure no invalid ids have been provided
          List<WarehouseData> supplyingWarehouses = new ArrayList<>();
          List<WarehouseData> allWarehouses = root.findAllWarehouses();
          List<ProductData> orderItemProducts = new ArrayList<>();
          List<ProductData> allProducts = root.findAllProducts();
          req.getItems()
              .forEach(
                  i -> {
                    supplyingWarehouses.add(
                        Find.warehouseById(i.getSupplyingWarehouseId(), allWarehouses));
                    orderItemProducts.add(Find.productById(i.getProductId(), allProducts));
                  });

          // Create a new order
          OrderData order = new OrderData();
          order.setCustomer(customer);
          order.setDistrict(district);
          order.setCarrier(null);
          order.setEntryDate(LocalDateTime.now());
          order.setItemCount(req.getItems().size());
          order.setAllLocal(
              req.getItems().stream()
                  .allMatch(line -> line.getSupplyingWarehouseId().equals(warehouse.getId())));
          district.getOrders().add(order);
          customer.getOrders().add(order);
          // Process individual order items
          List<OrderItemData> orderItems = toOrderItems(req.getItems(), order);
          List<NewOrderResponseItem> responseLines = new ArrayList<>(orderItems.size());
          double orderItemSum = 0;
          // Cache old stock state
          Map<String, StockData> originalStocks = new HashMap<>();
          Set<StockData> changedStocks = new HashSet<>();
          for (int i = 0; i < orderItems.size(); i++) {
            OrderItemData orderItem = orderItems.get(i);
            ProductData product = orderItemProducts.get(i);
            orderItem.setProduct(product);
            orderItem.setSupplyingWarehouse(supplyingWarehouses.get(i));
            StockData stock =
                warehouse.getStocks().parallelStream()
                    .filter(s -> s.getProduct().getId().equals(product.getId()))
                    .findAny()
                    .orElseThrow(DataNotFoundException::new);
            changedStocks.add(stock);
            if (!originalStocks.containsKey(stock.getId())) {
              originalStocks.put(stock.getId(), new StockData(stock));
            }
            NewOrderResponseItem responseLine = newOrderResponseLine(orderItem);
            responseLines.add(responseLine);
            int stockQuantity = stock.getQuantity();
            int orderItemQuantity = orderItem.getQuantity();
            stock.setQuantity(determineNewStockQuantity(stockQuantity, orderItemQuantity));
            stock.setYearToDateBalance(stock.getYearToDateBalance() + orderItemQuantity);
            stock.setOrderCount(stock.getOrderCount() + 1);
            responseLine.setStockQuantity(stock.getQuantity());
            responseLine.setItemName(product.getName());
            responseLine.setItemPrice(product.getPrice());
            responseLine.setAmount(product.getPrice() * orderItemQuantity);
            responseLine.setBrandGeneric(determineBrandGeneric(product.getData(), stock.getData()));
            orderItem.setAmount(product.getPrice() * orderItemQuantity);
            orderItem.setDeliveryDate(null);
            orderItem.setNumber(i + 1);
            orderItem.setDistInfo(getRandomDistrictInfo(stock));
            orderItemSum += orderItem.getAmount();
          }

          try {
            // Persist the changes
            Storer storer = storageManager.createEagerStorer();
            storer.storeAll(order, district.getOrders(), customer.getOrders(), changedStocks);
            storer.commit();
          } catch (RuntimeException e) {
            // Detach order object from graph
            district.getOrders().remove(order);
            customer.getOrders().remove(order);
            // Reset stocks
            for (StockData originalStock : originalStocks.values()) {
              StockData updatedStock =
                  warehouse.getStocks().parallelStream()
                      .filter(s -> s.getId().equals(originalStock.getId()))
                      .findAny()
                      .orElseThrow(DataNotFoundException::new);
              updatedStock.setQuantity(originalStock.getQuantity());
              updatedStock.setYearToDateBalance(originalStock.getYearToDateBalance());
              updatedStock.setOrderCount(originalStock.getOrderCount());
            }
            throw e;
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
    requestLine.setSupplyingWarehouseId(item.getSupplyingWarehouse().getId());
    requestLine.setItemId(item.getProduct().getId());
    requestLine.setItemPrice(0);
    requestLine.setAmount(item.getAmount());
    requestLine.setQuantity(item.getQuantity());
    requestLine.setStockQuantity(0);
    requestLine.setBrandGeneric(null);
    return requestLine;
  }

  private static List<OrderItemData> toOrderItems(
      List<NewOrderRequestItem> lines, OrderData order) {
    return lines.stream()
        .map(
            l -> {
              OrderItemData orderItem = new OrderItemData();
              ProductData product = new ProductData();
              product.setId(l.getProductId());
              WarehouseData warehouse = new WarehouseData();
              warehouse.setId(l.getSupplyingWarehouseId());
              orderItem.setProduct(product);
              orderItem.setSupplyingWarehouse(warehouse);
              orderItem.setQuantity(l.getQuantity());
              orderItem.setOrder(order);
              return orderItem;
            })
        .collect(Collectors.toList());
  }
}
