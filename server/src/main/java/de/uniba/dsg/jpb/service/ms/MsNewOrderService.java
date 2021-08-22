package de.uniba.dsg.jpb.service.ms;

import de.uniba.dsg.jpb.data.access.ms.CustomerRepository;
import de.uniba.dsg.jpb.data.access.ms.DataRoot;
import de.uniba.dsg.jpb.data.access.ms.ProductRepository;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.StockData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.OrderRequest;
import de.uniba.dsg.jpb.data.transfer.messages.OrderRequestItem;
import de.uniba.dsg.jpb.data.transfer.messages.OrderResponse;
import de.uniba.dsg.jpb.data.transfer.messages.OrderResponseItem;
import de.uniba.dsg.jpb.service.NewOrderService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsNewOrderService extends NewOrderService {

  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;

  public MsNewOrderService(DataRoot dataRoot) {
    productRepository = dataRoot.productRepository();
    // TODO
    customerRepository = null;
  }

  @Override
  public OrderResponse process(OrderRequest req) {
    // Fetch warehouse, district and customer
    CustomerData customer = customerRepository.getById(req.getCustomerId());
    DistrictData district = customer.getDistrict();
    WarehouseData warehouse = district.getWarehouse();

    // Create and persist a new order and order entry
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
    // FIXME we only need to persist customer.orders...
    customerRepository.save(customer);
    // Process individual order items
    List<OrderItemData> orderItems = toOrderItems(req.getItems(), order);
    List<OrderResponseItem> responseLines = new ArrayList<>(orderItems.size());
    double orderItemSum = 0;
    for (int i = 0; i < orderItems.size(); i++) {
      OrderItemData orderItem = orderItems.get(i);
      ProductData product = productRepository.getById(orderItem.getProduct().getId());
      StockData stock =
          warehouse.getStocks().stream()
              .filter(s -> s.getProduct().getId().equals(product.getId()))
              .findAny()
              .orElse(null);
      OrderResponseItem responseLine = newOrderResponseLine(orderItem);
      responseLines.add(responseLine);
      int stockQuantity = stock.getQuantity();
      int orderItemQuantity = orderItem.getQuantity();
      stock.setQuantity(determineNewStockQuantity(stockQuantity, orderItemQuantity));
      stock.setYearToDateBalance(stock.getYearToDateBalance() + orderItemQuantity);
      stock.setOrderCount(stock.getOrderCount() + 1);
      // TODO save stock here...
      responseLine.setStockQuantity(stock.getQuantity());
      responseLine.setItemName(product.getName());
      responseLine.setItemPrice(product.getPrice());
      responseLine.setAmount(product.getPrice() * orderItemQuantity);
      responseLine.setBrandGeneric(determineBrandGeneric(product.getData(), stock.getData()));
      orderItem.setAmount(product.getPrice() * orderItemQuantity);
      orderItem.setDeliveryDate(null);
      orderItem.setNumber(i + 1);
      orderItem.setDistInfo(getRandomDistrictInfo(stock));
      // TODO save orderItem
      orderItemSum += orderItem.getAmount();
    }

    // Prepare the response object
    OrderResponse res =
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
            orderItemSum, customer.getDiscount(), warehouse.getSalesTax(), district.getSalesTax()));
    res.setOrderItems(responseLines);
    return res;
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

  private static OrderResponseItem newOrderResponseLine(OrderItemData item) {
    OrderResponseItem requestLine = new OrderResponseItem();
    requestLine.setSupplyingWarehouseId(item.getSupplyingWarehouse().getId());
    requestLine.setItemId(item.getProduct().getId());
    requestLine.setItemPrice(0);
    requestLine.setAmount(item.getAmount());
    requestLine.setQuantity(item.getQuantity());
    requestLine.setStockQuantity(0);
    requestLine.setBrandGeneric(null);
    return requestLine;
  }

  private static List<OrderItemData> toOrderItems(List<OrderRequestItem> lines, OrderData order) {
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
