package de.uniba.dsg.jpb.server.service.jpa;

import de.uniba.dsg.jpb.data.model.jpa.CustomerEntity;
import de.uniba.dsg.jpb.data.model.jpa.OrderEntity;
import de.uniba.dsg.jpb.data.model.jpa.OrderItemEntity;
import de.uniba.dsg.jpb.messages.OrderItemStatusResponse;
import de.uniba.dsg.jpb.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.messages.OrderStatusResponse;
import de.uniba.dsg.jpb.server.data.access.jpa.CustomerRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.OrderItemRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.OrderRepository;
import de.uniba.dsg.jpb.server.service.OrderStatusService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaOrderStatusService extends OrderStatusService {

  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  @Autowired
  public JpaOrderStatusService(
      CustomerRepository customerRepository,
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository) {
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Override
  public OrderStatusResponse process(OrderStatusRequest req) {
    Long customerId = req.getCustomerId();
    CustomerEntity customer;
    if (customerId == null) {
      customer =
          customerRepository
              .findByEmail(req.getCustomerEmail())
              .orElseThrow(IllegalStateException::new);
      customerId = customer.getId();
    } else {
      customer = customerRepository.getById(customerId);
    }
    OrderEntity order =
        orderRepository
            .findMostRecentOrderOfCustomer(customerId)
            .orElseThrow(IllegalStateException::new);
    List<OrderItemEntity> orderItems =
        orderItemRepository.findByOrderIdOrderByNumberAsc(order.getId());
    return toOrderStatusResponse(req, order, customer, toOrderItemStatusResponse(orderItems));
  }

  private static List<OrderItemStatusResponse> toOrderItemStatusResponse(
      List<OrderItemEntity> orderItems) {
    List<OrderItemStatusResponse> responses = new ArrayList<>(orderItems.size());
    for (OrderItemEntity entity : orderItems) {
      OrderItemStatusResponse res = new OrderItemStatusResponse();
      res.setSupplyingWarehouseId(entity.getSupplyingWarehouse().getId());
      res.setProductId(entity.getProduct().getId());
      res.setQuantity(entity.getQuantity());
      res.setAmount(entity.getAmount());
      res.setDeliveryDate(entity.getDeliveryDate());
      responses.add(res);
    }
    return responses;
  }

  private static OrderStatusResponse toOrderStatusResponse(
      OrderStatusRequest req,
      OrderEntity order,
      CustomerEntity customer,
      List<OrderItemStatusResponse> itemStatusResponses) {
    OrderStatusResponse res = new OrderStatusResponse();
    res.setWarehouseId(req.getWarehouseId());
    res.setDistrictId(req.getDistrictId());
    res.setCustomerId(customer.getId());
    res.setCustomerFirstName(customer.getFirstName());
    res.setCustomerMiddleName(customer.getMiddleName());
    res.setCustomerLastName(customer.getLastName());
    res.setCustomerBalance(customer.getBalance());
    res.setOrderId(order.getId());
    res.setOrderEntryDate(order.getEntryDate());
    res.setOrderCarrierId(order.getCarrierId());
    res.setItemStatus(itemStatusResponses);
    return res;
  }
}
