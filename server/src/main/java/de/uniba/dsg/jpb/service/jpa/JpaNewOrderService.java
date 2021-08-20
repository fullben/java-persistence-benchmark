package de.uniba.dsg.jpb.service.jpa;

import de.uniba.dsg.jpb.data.access.jpa.CustomerRepository;
import de.uniba.dsg.jpb.data.access.jpa.DistrictRepository;
import de.uniba.dsg.jpb.data.access.jpa.OrderItemRepository;
import de.uniba.dsg.jpb.data.access.jpa.OrderRepository;
import de.uniba.dsg.jpb.data.access.jpa.ProductRepository;
import de.uniba.dsg.jpb.data.access.jpa.StockRepository;
import de.uniba.dsg.jpb.data.model.jpa.CustomerEntity;
import de.uniba.dsg.jpb.data.model.jpa.DistrictEntity;
import de.uniba.dsg.jpb.data.model.jpa.OrderEntity;
import de.uniba.dsg.jpb.data.model.jpa.OrderItemEntity;
import de.uniba.dsg.jpb.data.model.jpa.ProductEntity;
import de.uniba.dsg.jpb.data.model.jpa.StockEntity;
import de.uniba.dsg.jpb.data.model.jpa.WarehouseEntity;
import de.uniba.dsg.jpb.messages.OrderRequest;
import de.uniba.dsg.jpb.messages.OrderRequestItem;
import de.uniba.dsg.jpb.messages.OrderResponse;
import de.uniba.dsg.jpb.messages.OrderResponseItem;
import de.uniba.dsg.jpb.service.NewOrderService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaNewOrderService extends NewOrderService {

  private final ProductRepository productRepository;
  private final StockRepository stockRepository;
  private final OrderItemRepository orderItemRepository;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final DistrictRepository districtRepository;

  @Autowired
  public JpaNewOrderService(
      ProductRepository itemRepository,
      StockRepository stockRepository,
      OrderItemRepository orderItemRepository,
      OrderRepository orderRepository,
      CustomerRepository customerRepository,
      DistrictRepository districtRepository) {
    this.productRepository = itemRepository;
    this.stockRepository = stockRepository;
    this.orderItemRepository = orderItemRepository;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.districtRepository = districtRepository;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Override
  public OrderResponse process(OrderRequest req) {
    // 1. Fetch warehouse, district and customer
    CustomerEntity customer = customerRepository.getById(req.getCustomerId());
    DistrictEntity district = customer.getDistrict();
    if (district == null
        || !district.getId().equals(req.getDistrictId())
        || !district.getWarehouse().getId().equals(req.getWarehouseId())) {
      throw new IllegalArgumentException();
    }
    WarehouseEntity warehouse = district.getWarehouse();
    // 2. Create and persist a new order and order entry
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
    order = orderRepository.save(order);
    // 3. Process individual order items
    List<OrderItemEntity> orderItems = toOrderItems(req.getItems(), order);
    List<OrderResponseItem> responseLines = new ArrayList<>(orderItems.size());
    double orderItemSum = 0;
    for (int i = 0; i < orderItems.size(); i++) {
      OrderItemEntity orderItem = orderItems.get(i);
      ProductEntity product = productRepository.getById(orderItem.getProduct().getId());
      StockEntity stock =
          stockRepository
              .findByProductIdAndWarehouseId(
                  product.getId(), orderItem.getSupplyingWarehouse().getId())
              .orElseThrow(NullPointerException::new);
      OrderResponseItem responseLine = newOrderResponseLine(orderItem);
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
      orderItemRepository.save(orderItem);
      orderItemSum += orderItem.getAmount();
    }
    // 4. Prepare the response object
    OrderResponse res = newOrderResponse(req, order, warehouse, district, customer);
    res.setOrderId(order.getId());
    res.setOrderTimestamp(order.getEntryDate());
    res.setTotalAmount(
        calcOrderTotal(
            orderItemSum, customer.getDiscount(), warehouse.getSalesTax(), district.getSalesTax()));
    res.setOrderItems(responseLines);
    return res;
  }

  private static OrderResponse newOrderResponse(
      OrderRequest req,
      OrderEntity order,
      WarehouseEntity warehouse,
      DistrictEntity district,
      CustomerEntity customer) {
    OrderResponse res = new OrderResponse(req);
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

  private static String getRandomDistrictInfo(StockEntity stock) {
    return getDistrictInfo(randomDistrictNumber(), stock);
  }

  private static String getDistrictInfo(int districtNbr, StockEntity stock) {
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

  private static OrderResponseItem newOrderResponseLine(OrderItemEntity item) {
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

  private static List<OrderItemEntity> toOrderItems(
      List<OrderRequestItem> lines, OrderEntity order) {
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
