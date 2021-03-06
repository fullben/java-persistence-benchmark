package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.access.CustomerRepository;
import de.uniba.dsg.wss.data.access.OrderRepository;
import de.uniba.dsg.wss.data.access.ProductRepository;
import de.uniba.dsg.wss.data.access.StockRepository;
import de.uniba.dsg.wss.data.access.WarehouseRepository;
import de.uniba.dsg.wss.data.model.BaseEntity;
import de.uniba.dsg.wss.data.model.CustomerEntity;
import de.uniba.dsg.wss.data.model.DistrictEntity;
import de.uniba.dsg.wss.data.model.OrderEntity;
import de.uniba.dsg.wss.data.model.OrderItemEntity;
import de.uniba.dsg.wss.data.model.ProductEntity;
import de.uniba.dsg.wss.data.model.StockEntity;
import de.uniba.dsg.wss.data.model.WarehouseEntity;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponseItem;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaNewOrderService extends NewOrderService {

  private final WarehouseRepository warehouseRepository;
  private final ProductRepository productRepository;
  private final StockRepository stockRepository;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;

  @Autowired
  public JpaNewOrderService(
      WarehouseRepository warehouseRepository,
      ProductRepository productRepository,
      StockRepository stockRepository,
      OrderRepository orderRepository,
      CustomerRepository customerRepository) {
    this.warehouseRepository = warehouseRepository;
    this.productRepository = productRepository;
    this.stockRepository = stockRepository;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
  }

  @Retryable(
      value = {RuntimeException.class, SQLException.class, PSQLException.class},
      backoff = @Backoff(delay = 100),
      maxAttempts = 5)
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  @Override
  public NewOrderResponse process(NewOrderRequest req) {
    // Fetch warehouse, district and customer
    CustomerEntity customer = customerRepository.getById(req.getCustomerId());
    DistrictEntity district = customer.getDistrict();
    if (district == null
        || !district.getId().equals(req.getDistrictId())
        || !district.getWarehouse().getId().equals(req.getWarehouseId())) {
      throw new IllegalArgumentException();
    }
    WarehouseEntity warehouse = district.getWarehouse();

    // Create and persist a new order
    OrderEntity order = new OrderEntity();
    order.setCustomer(customer);
    order.setDistrict(district);
    order.setCarrier(null);
    order.setEntryDate(LocalDateTime.now());
    order.setItemCount(req.getItems().size());
    // We're not all local if any item is supplied by a non-home warehouse
    order.setAllLocal(
        req.getItems().stream()
            .allMatch(line -> line.getSupplyingWarehouseId().equals(warehouse.getId())));

    // Process individual order items
    List<OrderItemEntity> orderItems = toOrderItems(req.getItems(), order);
    List<NewOrderResponseItem> responseLines = new ArrayList<>(orderItems.size());
    double orderItemSum = 0;
    // Find all supplying warehouses and products
    Set<String> supplyingWarehouseIds =
        orderItems.stream().map(i -> i.getSupplyingWarehouse().getId()).collect(Collectors.toSet());
    Map<String, WarehouseEntity> supplyingWarehouses =
        warehouseRepository.findAllById(supplyingWarehouseIds).stream()
            .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
    if (supplyingWarehouseIds.size() != supplyingWarehouses.size()) {
      throw new IllegalStateException();
    }
    Set<String> orderProductIds =
        orderItems.stream().map(i -> i.getProduct().getId()).collect(Collectors.toSet());
    Map<String, ProductEntity> orderProducts =
        productRepository.findAllById(orderProductIds).stream()
            .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

    for (int i = 0; i < orderItems.size(); i++) {
      OrderItemEntity orderItem = orderItems.get(i);
      orderItem.setSupplyingWarehouse(
          supplyingWarehouses.get(orderItem.getSupplyingWarehouse().getId()));
      orderItem.setProduct(orderProducts.get(orderItem.getProduct().getId()));
      ProductEntity product = productRepository.getById(orderItem.getProduct().getId());
      StockEntity stock =
          stockRepository
              .findByProductIdAndWarehouseId(
                  product.getId(), orderItem.getSupplyingWarehouse().getId())
              .orElseThrow(IllegalStateException::new);
      NewOrderResponseItem responseLine = newOrderResponseLine(orderItem);
      responseLines.add(responseLine);
      int stockQuantity = stock.getQuantity();
      int orderItemQuantity = orderItem.getQuantity();
      stock.setQuantity(determineNewStockQuantity(stockQuantity, orderItemQuantity));
      stock.setYearToDateBalance(stock.getYearToDateBalance() + orderItemQuantity);
      stock.setOrderCount(stock.getOrderCount() + 1);
      stock = stockRepository.save(stock);
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
    order.setItems(orderItems);
    // Save order and items
    order = orderRepository.save(order);

    // Prepare the response object
    NewOrderResponse res = newOrderResponse(req, order, warehouse, district, customer);
    res.setOrderId(order.getId());
    res.setOrderTimestamp(order.getEntryDate());
    res.setTotalAmount(
        calcOrderTotal(
            orderItemSum, customer.getDiscount(), warehouse.getSalesTax(), district.getSalesTax()));
    res.setOrderItems(responseLines);
    return res;
  }

  private static NewOrderResponse newOrderResponse(
      NewOrderRequest req,
      OrderEntity order,
      WarehouseEntity warehouse,
      DistrictEntity district,
      CustomerEntity customer) {
    NewOrderResponse res = new NewOrderResponse(req);
    res.setOrderId(order.getId());
    res.setOrderTimestamp(order.getEntryDate());
    res.setWarehouseSalesTax(warehouse.getSalesTax());
    res.setDistrictSalesTax(district.getSalesTax());
    res.setOrderItemCount(req.getItems().size());
    res.setCustomerCredit(customer.getCredit());
    res.setCustomerDiscount(customer.getDiscount());
    res.setCustomerLastName(customer.getLastName());
    return res;
  }

  private String getRandomDistrictInfo(StockEntity stock) {
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

  private static NewOrderResponseItem newOrderResponseLine(OrderItemEntity item) {
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

  private static List<OrderItemEntity> toOrderItems(
      List<NewOrderRequestItem> lines, OrderEntity order) {
    return lines.stream()
        .map(
            l -> {
              OrderItemEntity orderItem = new OrderItemEntity();
              ProductEntity product = new ProductEntity();
              product.setId(l.getProductId());
              WarehouseEntity warehouse = new WarehouseEntity();
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
