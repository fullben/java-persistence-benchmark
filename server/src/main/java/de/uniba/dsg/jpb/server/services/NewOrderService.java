package de.uniba.dsg.jpb.server.services;

import de.uniba.dsg.jpb.messages.OrderRequest;
import de.uniba.dsg.jpb.messages.OrderRequestLine;
import de.uniba.dsg.jpb.messages.OrderResponse;
import de.uniba.dsg.jpb.messages.OrderResponseLine;
import de.uniba.dsg.jpb.server.model.Customer;
import de.uniba.dsg.jpb.server.model.District;
import de.uniba.dsg.jpb.server.model.Item;
import de.uniba.dsg.jpb.server.model.NewOrder;
import de.uniba.dsg.jpb.server.model.Order;
import de.uniba.dsg.jpb.server.model.OrderLine;
import de.uniba.dsg.jpb.server.model.Stock;
import de.uniba.dsg.jpb.server.model.Warehouse;
import de.uniba.dsg.jpb.server.repositories.CustomerRepository;
import de.uniba.dsg.jpb.server.repositories.DistrictRepository;
import de.uniba.dsg.jpb.server.repositories.ItemRepository;
import de.uniba.dsg.jpb.server.repositories.NewOrderRepository;
import de.uniba.dsg.jpb.server.repositories.OrderLineRepository;
import de.uniba.dsg.jpb.server.repositories.OrderRepository;
import de.uniba.dsg.jpb.server.repositories.StockRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewOrderService {

  private final ItemRepository itemRepository;
  private final StockRepository stockRepository;
  private final OrderLineRepository orderLineRepository;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final DistrictRepository districtRepository;
  private final NewOrderRepository newOrderRepository;

  @Autowired
  public NewOrderService(
      ItemRepository itemRepository,
      StockRepository stockRepository,
      OrderLineRepository orderLineRepository,
      OrderRepository orderRepository,
      CustomerRepository customerRepository,
      DistrictRepository districtRepository,
      NewOrderRepository newOrderRepository) {
    this.itemRepository = itemRepository;
    this.stockRepository = stockRepository;
    this.orderLineRepository = orderLineRepository;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.districtRepository = districtRepository;
    this.newOrderRepository = newOrderRepository;
  }

  @Transactional
  public OrderResponse process(OrderRequest req) {
    // 1. Fetch warehouse, district and customer
    Customer customer = customerRepository.findById(req.getCustomerId()).orElse(null);
    if (customer == null) {
      throw new IllegalArgumentException();
    }
    District district = customer.getDistrict();
    if (district == null
        || !district.getId().equals(req.getDistrictId())
        || !district.getWarehouse().getId().equals(req.getWarehouseId())) {
      throw new IllegalArgumentException();
    }
    Warehouse warehouse = district.getWarehouse();
    if (warehouse == null) {
      throw new IllegalArgumentException();
    }
    Long nextOrderId = district.getNextOrderId();
    // TODO persist district
    district.setNextOrderId(nextOrderId + 1);
    district = districtRepository.save(district);
    // 2. Create and persist a new order and order entry
    Order order = new Order();
    order.setId(nextOrderId);
    order.setCustomer(customer);
    order.setDistrict(district);
    order.setCarrierId(null);
    order.setEntryDate(LocalDateTime.now());
    order.setOrderLineCount(req.getLines().size());
    // We're not all local if any item is supplied by a non-home warehouse
    order.setAllLocal(
        req.getLines().stream()
                .anyMatch(line -> !line.getSupplyingWarehouseId().equals(warehouse.getId()))
            ? 0
            : 1);
    order = orderRepository.save(order);
    NewOrder newOrder = new NewOrder();
    newOrder.setOrder(order);
    newOrderRepository.save(newOrder);
    // 3. Process individual order lines
    List<OrderLine> orderLines = toOrderLines(req.getLines());
    List<OrderResponseLine> responseLines = new ArrayList<>(orderLines.size());
    double orderLineSum = 0;
    for (OrderLine orderLine : orderLines) {
      // This may result in null due to requirement of 1% of all transactions using an unused id
      Item item = itemRepository.findById(orderLine.getItem().getId()).orElse(null);
      if (item == null) {
        // TODO this is not really appropriate, how do I roll back the transaction now
        OrderResponse res = newOrderResponse(req, order, warehouse, district, customer);
        res.setTotalAmount(0);
        res.setOrderLines(null);
        res.setMessage("Item number is not valid");
        return res;
      }
      Stock stock =
          stockRepository
              .findByItemIdAndWarehouseId(
                  item.getId(), orderLine.getOrder().getDistrict().getWarehouse())
              .orElseThrow(NullPointerException::new);
      OrderResponseLine responseLine = newOrderResponseLine(orderLine);
      responseLines.add(responseLine);
      int stockQuantity = stock.getQuantity();
      int orderLineQuantity = orderLine.getQuantity();
      if (stockQuantity + 10 > orderLineQuantity) {
        stock.setQuantity(stockQuantity - orderLineQuantity);
      } else {
        stock.setQuantity(stockQuantity - orderLineQuantity + 91);
      }
      stock.setYearToDateBalance(stock.getYearToDateBalance() + orderLineQuantity);
      stock.setOrderCount(stock.getOrderCount() + 1);
      // TODO this ain't gud, how can we roll this back?
      stock = stockRepository.save(stock);
      responseLine.setStockQuantity(stock.getQuantity());
      responseLine.setItemName(item.getName());
      responseLine.setItemPrice(item.getPrice());
      responseLine.setAmount(item.getPrice() * orderLineQuantity);
      responseLine.setBrandGeneric(determineBrandGeneric(item.getData(), stock.getData()));
      orderLine.setAmount(item.getPrice() * orderLineQuantity);
      orderLine.setDeliveryDate(null);
      orderLine.setNumber(orderLines.indexOf(orderLine) + 1);
      orderLine.setDistInfo(
          getDistrictInfo(orderLine.getOrder().getDistrict().getId().intValue(), stock));
      orderLineRepository.save(orderLine);
      orderLineSum += orderLine.getAmount();
    }
    // 4. Prepare the response object
    OrderResponse res = newOrderResponse(req, order, warehouse, district, customer);
    res.setOrderId(order.getId());
    res.setOrderTimestamp(order.getEntryDate());
    res.setTotalAmount(
        calcOrderTotal(
            orderLineSum, customer.getDiscount(), warehouse.getSalesTax(), district.getSalesTax()));
    res.setOrderLines(responseLines);
    return res;
  }

  private static OrderResponse newOrderResponse(
      OrderRequest req, Order order, Warehouse warehouse, District district, Customer customer) {
    OrderResponse res = new OrderResponse(req);
    res.setOrderId(order.getId());
    res.setOrderTimestamp(order.getEntryDate());
    res.setWarehouseSalesTax(warehouse.getSalesTax());
    res.setDistrictSalesTax(district.getSalesTax());
    res.setOrderLineCount(req.getLines().size());
    res.setCustomerCredit(customer.getCredit());
    res.setCustomerDiscount(customer.getDiscount());
    res.setCustomerLastName(customer.getLastName());
    return res;
  }

  private static String determineBrandGeneric(String itemData, String stockData) {
    final String s = "ORIGINAL";
    return itemData.contains(s) || stockData.contains(s) ? "B" : "G";
  }

  private static double calcOrderTotal(
      double sumPrice, double customerDiscount, double warehouseSalesTax, double districtSalesTax) {
    if (sumPrice < 0
        || customerDiscount < 0
        || customerDiscount > 1
        || warehouseSalesTax < 0
        || districtSalesTax < 0) {
      throw new IllegalArgumentException();
    }
    return sumPrice * (1 - customerDiscount) * (1 + warehouseSalesTax + districtSalesTax);
  }

  private static String getDistrictInfo(int districtNbr, Stock stock) {
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

  private static OrderResponseLine newOrderResponseLine(OrderLine line) {
    OrderResponseLine requestLine = new OrderResponseLine();
    requestLine.setSupplyingWarehouseId(line.getSupplyingWarehouse().getId());
    requestLine.setItemId(line.getItem().getId());
    requestLine.setItemPrice(0);
    requestLine.setAmount(line.getAmount());
    requestLine.setQuantity(line.getQuantity());
    requestLine.setStockQuantity(0);
    requestLine.setBrandGeneric(null);
    return requestLine;
  }

  private List<OrderLine> toOrderLines(List<OrderRequestLine> lines) {
    return lines.stream()
        .map(
            l -> {
              OrderLine line = new OrderLine();
              // FIXME we somehow need to fetch the supplying warehouse and item
              line.setItem(null);
              line.setSupplyingWarehouse(null);
              line.setQuantity(l.getQuantity());
              return line;
            })
        .collect(Collectors.toList());
  }
}
