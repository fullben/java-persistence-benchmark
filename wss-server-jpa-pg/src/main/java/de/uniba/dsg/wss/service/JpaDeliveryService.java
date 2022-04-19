package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.access.CarrierRepository;
import de.uniba.dsg.wss.data.access.CustomerRepository;
import de.uniba.dsg.wss.data.access.DistrictRepository;
import de.uniba.dsg.wss.data.access.OrderRepository;
import de.uniba.dsg.wss.data.model.CarrierEntity;
import de.uniba.dsg.wss.data.model.CustomerEntity;
import de.uniba.dsg.wss.data.model.DistrictEntity;
import de.uniba.dsg.wss.data.model.OrderEntity;
import de.uniba.dsg.wss.data.model.OrderItemEntity;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaDeliveryService extends DeliveryService {

  private final DistrictRepository districtRepository;
  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final CarrierRepository carrierRepository;

  @Autowired
  public JpaDeliveryService(
      DistrictRepository districtRepository,
      OrderRepository orderRepository,
      CustomerRepository customerRepository,
      CarrierRepository carrierRepository) {
    this.districtRepository = districtRepository;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.carrierRepository = carrierRepository;
  }

  @Retryable(
      value = {RuntimeException.class, SQLException.class, PSQLException.class},
      backoff = @Backoff(delay = 100),
      maxAttempts = 5)
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  @Override
  public DeliveryResponse process(DeliveryRequest req) {
    // Find districts and carrier
    List<DistrictEntity> districts = districtRepository.findByWarehouseId(req.getWarehouseId());
    CarrierEntity carrier = carrierRepository.getById(req.getCarrierId());

    List<String> districtIds =
        districts.stream().map(DistrictEntity::getId).collect(Collectors.toList());
    List<OrderEntity> orders = orderRepository.findOldestUnfulfilledOrderOfDistricts(districtIds);
    Map<String, CustomerEntity> customers = new HashMap<>();

    for (OrderEntity order : orders) {

      double amountSum = 0;

      // Update fulfillment status and carrier of order
      order.setCarrier(carrier);
      order.setFulfilled(true);

      // Find all order items, set delivery date to now and sum amount
      for (OrderItemEntity orderItem : order.getItems()) {
        orderItem.setDeliveryDate(LocalDateTime.now());
        amountSum += orderItem.getAmount();
      }

      // Update customer balance and delivery count
      CustomerEntity customer = order.getCustomer();
      customer.setBalance(customer.getBalance() + amountSum);
      customer.setDeliveryCount(customer.getDeliveryCount() + 1);
      customers.putIfAbsent(customer.getId(), customer);
    }

    // save - batch processing
    orderRepository.saveAll(orders);
    customerRepository.saveAll(customers.values());
    return new DeliveryResponse(req);
  }
}
