package de.uniba.dsg.jpb.server.service.ms;

import de.uniba.dsg.jpb.server.data.access.ms.CustomerRepository;
import de.uniba.dsg.jpb.server.data.access.ms.DataRoot;
import de.uniba.dsg.jpb.server.data.access.ms.ProductRepository;
import de.uniba.dsg.jpb.server.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.server.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.server.data.model.ms.OrderData;
import de.uniba.dsg.jpb.server.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.server.data.model.ms.ProductData;
import de.uniba.dsg.jpb.server.data.model.ms.StockData;
import de.uniba.dsg.jpb.server.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.server.messages.OrderRequest;
import de.uniba.dsg.jpb.server.messages.OrderRequestItem;
import de.uniba.dsg.jpb.server.messages.OrderResponse;
import de.uniba.dsg.jpb.server.messages.OrderResponseItem;
import de.uniba.dsg.jpb.server.service.NewOrderService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsNewOrderService extends NewOrderService {

  private final DataRoot dataRoot;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;

  public MsNewOrderService(DataRoot dataRoot) {
    this.dataRoot = dataRoot;
    productRepository = dataRoot.getProductRepository();
    // TODO
    customerRepository = null;
  }

  @Override
  public OrderResponse process(OrderRequest req) {
    // 1. Fetch warehouse, district and customer
    CustomerData customer = customerRepository.findById(req.getCustomerId());
    DistrictData district = customer.getDistrict();
    WarehouseData warehouse = district.getWarehouse();
    // 2. Create and persist a new order and order entry
    OrderData order = new OrderData();
    order.setCustomer(customer);
    order.setDistrict(district);
    order.setCarrier(null);
    order.setEntryDate(LocalDateTime.now());
    order.setItemCount(req.getItems().size());
    order.setAllLocal(
        req.getItems().stream()
            .allMatch(line -> line.getSupplyingWarehouseId().equals(warehouse.getId())));
    customer.getOrders().add(order);
    // FIXME we only need to persist customer.orders...
    customerRepository.save(customer);
    // 3. Process individual order items
    List<OrderItemData> orderItems = toOrderItems(req.getItems(), order);
    List<OrderResponseItem> responseLines = new ArrayList<>(orderItems.size());
    double orderItemSum = 0;
    for (int i = 0; i < orderItems.size(); i++) {
      OrderItemData orderItem = orderItems.get(i);
      ProductData product = productRepository.findById(orderItem.getProduct().getId());
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
    // 4. Prepare the response object
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

  private static String getRandomDistrictInfo(StockData stock) {
    return getDistrictInfo(randomDistrictNumber(), stock);
  }

  private static String getDistrictInfo(int districtNbr, StockData stock) {
    switch (districtNbr) {
      case 1:
        return stock.getDist01();
      case 2:
        return stock.getDist02();
      case 3:
        return stock.getDist03();
      case 4:
        return stock.getDist04();
      case 5:
        return stock.getDist05();
      case 6:
        return stock.getDist06();
      case 7:
        return stock.getDist07();
      case 8:
        return stock.getDist08();
      case 9:
        return stock.getDist09();
      case 10:
        return stock.getDist10();
      default:
        throw new IllegalArgumentException();
    }
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
