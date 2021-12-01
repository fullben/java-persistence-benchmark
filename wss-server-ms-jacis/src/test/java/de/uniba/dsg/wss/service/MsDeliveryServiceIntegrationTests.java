package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.OrderItemData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import java.util.List;
import org.jacis.container.JacisContainer;
import org.jacis.plugin.txadapter.local.JacisLocalTransaction;
import org.jacis.store.JacisStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsDeliveryServiceIntegrationTests extends MicroStreamServiceTest {

  @Autowired private JacisContainer container;
  @Autowired private JacisStore<String, WarehouseData> warehouseStore;
  @Autowired private JacisStore<String, DistrictData> districtStore;
  @Autowired private JacisStore<String, CustomerData> customerStore;
  @Autowired private JacisStore<String, OrderData> orderStore;
  @Autowired private JacisStore<String, OrderItemData> orderItemStore;
  @Autowired private JacisStore<String, CarrierData> carrierStore;
  private MsDeliveryService deliveryService;
  private DeliveryRequest request;
  private String orderId;

  @BeforeEach
  public void setUp() {
    populateStorage();
    JacisLocalTransaction tx = container.beginLocalTransaction("Delivery test setup");
    request = new DeliveryRequest();

    // WO
    WarehouseData warehouse = warehouseStore.getAllReadOnly(w -> w.getId().equals("W0")).get(0);
    // D0
    DistrictData district = districtStore.getAllReadOnly(d -> d.getId().equals("D0")).get(0);
    request.setWarehouseId(warehouse.getId());
    // CC0
    request.setCarrierId(carrierStore.getAllReadOnly().get(0).getId());

    // Ensure that single order is not fulfilled
    // O0
    OrderData order = orderStore.getAll(o -> o.getId().equals("O0")).get(0);
    orderId = order.getId();
    order.setCarrierId(null);
    order.setFulfilled(false);

    List<OrderItemData> orderItems =
        orderItemStore.getAll(i -> i.getOrderId().equals(order.getId()));
    orderItems.forEach(
        i -> {
          i.setDeliveryDate(null);
          orderItemStore.update(i.getId(), i);
        });

    orderStore.update(order.getId(), order);
    tx.commit();
    deliveryService =
        new MsDeliveryService(
            container,
            warehouseStore,
            districtStore,
            customerStore,
            orderStore,
            orderItemStore,
            carrierStore);
  }

  @Test
  public void deliveryProcessingReturnsExpectedValues() {
    DeliveryResponse res = deliveryService.process(request);

    assertEquals(request.getWarehouseId(), res.getWarehouseId());
    assertEquals(request.getCarrierId(), res.getCarrierId());
  }

  @Test
  public void orderIsUpdatedByDeliveryRequest() {
    deliveryService.process(request);

    OrderData order = orderStore.getReadOnly(orderId);
    assertTrue(order.isFulfilled());
    assertNotNull(order.getCarrierId());
    List<OrderItemData> orderItems =
        orderItemStore.getAllReadOnly(i -> i.getOrderId().equals(orderId));
    assertEquals(order.getItemCount(), orderItems.size());
    assertFalse(orderItems.isEmpty());
    for (OrderItemData item : orderItems) {
      assertNotNull(item.getDeliveryDate());
    }
  }

  @AfterEach
  public void tearDown() {
    container.clearAllStores();
  }
}
