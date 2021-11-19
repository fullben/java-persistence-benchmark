package de.uniba.dsg.wss.ms.service;

import de.uniba.dsg.wss.data.transfer.messages.OrderItemStatusResponse;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.wss.ms.data.model.CustomerData;
import de.uniba.dsg.wss.ms.data.model.DistrictData;
import de.uniba.dsg.wss.ms.data.model.OrderData;
import de.uniba.dsg.wss.ms.data.model.OrderItemData;
import de.uniba.dsg.wss.ms.data.model.WarehouseData;
import de.uniba.dsg.wss.service.OrderStatusService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.jacis.container.JacisContainer;
import org.jacis.store.JacisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MsOrderStatusService extends OrderStatusService {

  private final JacisContainer container;
  private final JacisStore<String, WarehouseData> warehouseStore;
  private final JacisStore<String, DistrictData> districtStore;
  private final JacisStore<String, CustomerData> customerStore;
  private final JacisStore<String, OrderData> orderStore;
  private final JacisStore<String, OrderItemData> orderItemStore;

  @Autowired
  public MsOrderStatusService(
      JacisContainer container,
      JacisStore<String, WarehouseData> warehouseStore,
      JacisStore<String, DistrictData> districtStore,
      JacisStore<String, CustomerData> customerStore,
      JacisStore<String, OrderData> orderStore,
      JacisStore<String, OrderItemData> orderItemStore) {
    this.container = container;
    this.warehouseStore = warehouseStore;
    this.districtStore = districtStore;
    this.customerStore = customerStore;
    this.orderStore = orderStore;
    this.orderItemStore = orderItemStore;
  }

  @Override
  public OrderStatusResponse process(OrderStatusRequest req) {
    // Fetch warehouse, district, and customer (either by id or email)
    WarehouseData warehouse = warehouseStore.getReadOnly(req.getWarehouseId());
    DistrictData district = districtStore.getReadOnly(req.getDistrictId());
    String customerId = req.getCustomerId();
    CustomerData customer;
    if (customerId == null) {
      customer =
          customerStore
              .streamReadOnly()
              .parallel()
              .filter(c -> c.getEmail().equals(req.getCustomerEmail()))
              .findAny()
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "Failed to find customer with email " + req.getCustomerEmail()));
    } else {
      customer =
          customerStore
              .streamReadOnly()
              .parallel()
              .filter(c -> c.getId().equals(customerId))
              .findAny()
              .orElseThrow(
                  () -> new IllegalStateException("Failed to find customer with id " + customerId));
    }

    // Find the most recent order of the customer and parse the delivery dates (and some other
    // info)
    OrderData order =
        orderStore
            .streamReadOnly()
            .parallel()
            .filter(o -> o.getCustomerId().equals(customer.getId()))
            .max(Comparator.comparing(OrderData::getEntryDate))
            .orElseThrow(IllegalStateException::new);

    List<OrderItemData> orderItems =
        orderItemStore
            .streamReadOnly(i -> i.getOrderId().equals(order.getId()))
            .parallel()
            .collect(Collectors.toList());

    return toOrderStatusResponse(req, order, customer, toOrderItemStatusResponse(orderItems));
  }

  private static List<OrderItemStatusResponse> toOrderItemStatusResponse(
      List<OrderItemData> orderItems) {
    List<OrderItemStatusResponse> responses = new ArrayList<>(orderItems.size());
    for (OrderItemData item : orderItems) {
      OrderItemStatusResponse res = new OrderItemStatusResponse();
      res.setSupplyingWarehouseId(item.getSupplyingWarehouseId());
      res.setProductId(item.getProductId());
      res.setQuantity(item.getQuantity());
      res.setAmount(item.getAmount());
      res.setDeliveryDate(item.getDeliveryDate());
      responses.add(res);
    }
    return responses;
  }

  private static OrderStatusResponse toOrderStatusResponse(
      OrderStatusRequest req,
      OrderData order,
      CustomerData customer,
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
