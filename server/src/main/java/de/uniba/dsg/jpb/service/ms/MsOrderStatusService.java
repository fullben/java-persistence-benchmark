package de.uniba.dsg.jpb.service.ms;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.DataNotFoundException;
import de.uniba.dsg.jpb.data.access.ms.Find;
import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.OrderItemStatusResponse;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.jpb.service.OrderStatusService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsOrderStatusService extends OrderStatusService {

  private final DataManager dataManager;

  public MsOrderStatusService(DataManager dataManager) {
    this.dataManager = dataManager;
  }

  @Override
  public OrderStatusResponse process(OrderStatusRequest req) {
    return dataManager.read(
        (root) -> {
          // Fetch warehouse, district, and customer (either by id or email)
          WarehouseData warehouse =
              Find.warehouseById(req.getWarehouseId(), root.findAllWarehouses());
          DistrictData district = Find.districtById(req.getDistrictId(), warehouse);
          String customerId = req.getCustomerId();
          CustomerData customer;
          if (customerId == null) {
            customer = Find.customerByEmail(req.getCustomerEmail(), district);
          } else {
            customer = Find.customerById(customerId, district);
          }

          // Find the most recent order of the customer and parse the delivery dates (and some other
          // info)
          OrderData order =
              Find.mostRecentOrderOfCustomer(customer.getId(), district)
                  .orElseThrow(DataNotFoundException::new);
          return toOrderStatusResponse(
              req, order, customer, toOrderItemStatusResponse(order.getItems()));
        });
  }

  private static List<OrderItemStatusResponse> toOrderItemStatusResponse(
      List<OrderItemData> orderItems) {
    List<OrderItemStatusResponse> responses = new ArrayList<>(orderItems.size());
    for (OrderItemData entity : orderItems) {
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
    CarrierData carrier = order.getCarrier();
    res.setOrderCarrierId(carrier != null ? carrier.getId() : null);
    res.setItemStatus(itemStatusResponses);
    return res;
  }
}
