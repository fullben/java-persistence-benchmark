package de.uniba.dsg.wss.ms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.ms.data.gen.MsDataWriter;
import de.uniba.dsg.wss.ms.data.model.CarrierData;
import de.uniba.dsg.wss.ms.data.model.CustomerData;
import de.uniba.dsg.wss.ms.data.model.DistrictData;
import de.uniba.dsg.wss.ms.data.model.OrderData;
import de.uniba.dsg.wss.ms.data.model.OrderItemData;
import de.uniba.dsg.wss.ms.data.model.WarehouseData;
import java.util.List;
import org.jacis.container.JacisContainer;
import org.jacis.plugin.txadapter.local.JacisLocalTransaction;
import org.jacis.store.JacisStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class MsDeliveryServiceIntegrationTests extends MicroStreamServiceTest {

  @Autowired private JacisContainer container;
  @Autowired private JacisStore<String, WarehouseData> warehouseStore;
  @Autowired private JacisStore<String, DistrictData> districtStore;
  @Autowired private JacisStore<String, CustomerData> customerStore;
  @Autowired private JacisStore<String, OrderData> orderStore;
  @Autowired private JacisStore<String, OrderItemData> orderItemStore;
  @Autowired private JacisStore<String, CarrierData> carrierStore;
  @Autowired private MsDataWriter dataWriter;
  private MsDeliveryService deliveryService;
  private DeliveryRequest request;
  private String orderId;

  @BeforeEach
  public void setUp() {
    populateStorage(new DataGenerator(1, 1, 1, 1, 1_000, new BCryptPasswordEncoder()), dataWriter);
    JacisLocalTransaction tx = container.beginLocalTransaction("Delivery test setup");
    request = new DeliveryRequest();

    WarehouseData warehouse = warehouseStore.getAllReadOnly().get(0);
    DistrictData district =
        districtStore.getAllReadOnly(d -> d.getWarehouseId().equals(warehouse.getId())).get(0);
    request.setWarehouseId(warehouse.getId());
    request.setCarrierId(carrierStore.getAllReadOnly().get(0).getId());

    // Ensure that single order is not fulfilled
    OrderData order = orderStore.getAll(o -> o.getDistrictId().equals(district.getId())).get(0);
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
