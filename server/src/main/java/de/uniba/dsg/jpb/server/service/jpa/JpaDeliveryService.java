package de.uniba.dsg.jpb.server.service.jpa;

import de.uniba.dsg.jpb.data.model.jpa.CustomerEntity;
import de.uniba.dsg.jpb.data.model.jpa.DistrictEntity;
import de.uniba.dsg.jpb.data.model.jpa.OrderEntity;
import de.uniba.dsg.jpb.data.model.jpa.OrderItemEntity;
import de.uniba.dsg.jpb.messages.DeliveryRequest;
import de.uniba.dsg.jpb.messages.DeliveryResponse;
import de.uniba.dsg.jpb.server.data.access.jpa.CustomerRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.DistrictRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.OrderItemRepository;
import de.uniba.dsg.jpb.server.data.access.jpa.OrderRepository;
import de.uniba.dsg.jpb.server.service.DeliveryService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaDeliveryService extends DeliveryService {

  private final DistrictRepository districtRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final CustomerRepository customerRepository;

  @Autowired
  public JpaDeliveryService(
      DistrictRepository districtRepository,
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      CustomerRepository customerRepository) {
    this.districtRepository = districtRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.customerRepository = customerRepository;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Override
  public DeliveryResponse process(DeliveryRequest req) {
    List<DistrictEntity> districts = districtRepository.findByWarehouseId(req.getWarehouseId());
    for (DistrictEntity district : districts) {
      double amountSum = 0;
      // Find oldest new/unfulfilled order
      OrderEntity order =
          orderRepository.findOldestUnfulfilledOrderOfDistrict(district.getId()).orElse(null);
      if (order == null) {
        // No unfulfilled orders for this district, do nothing
        continue;
      }
      // Update carrier id of order
      order.setCarrierId(req.getCarrierId());
      order = orderRepository.save(order);
      // Find all order items, set delivery date to now and sum amount
      List<OrderItemEntity> orderItems =
          orderItemRepository.findByOrderIdOrderByNumberAsc(order.getId());
      for (OrderItemEntity orderItem : orderItems) {
        orderItem.setDeliveryDate(LocalDateTime.now());
        amountSum += orderItem.getAmount();
      }
      orderItemRepository.saveAll(orderItems);
      // Update fulfillment status of order
      order.setFulfilled(true);
      order = orderRepository.save(order);
      // Update customer balance and delivery count
      CustomerEntity customer = order.getCustomer();
      customer.setBalance(customer.getBalance() + amountSum);
      customer.setDeliveryCount(customer.getDeliveryCount() + 1);
      customerRepository.save(customer);
    }
    return new DeliveryResponse(req);
  }
}
