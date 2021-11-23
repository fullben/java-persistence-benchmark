package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.service.ms.MsDeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MsDeliveryServiceIntegrationTests extends MicroStreamServiceTest {

  @Autowired
  private MsDeliveryService deliveryService;
  @Autowired
  private MsDataRoot dataRoot;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
  }

  @Test
  public void deliveryProcessingReturnsExpectedValues() {
    DeliveryRequest request = new DeliveryRequest("W0", "CC0");
    DeliveryResponse response = deliveryService.process(request);

    assertEquals(request.getWarehouseId(), response.getWarehouseId());
    assertEquals(request.getCarrierId(), response.getCarrierId());
  }

  @Test
  public void checkIfOldestOrderIsUpdated() {
    DeliveryRequest request = new DeliveryRequest("W0", "CC0");
    DeliveryResponse response = deliveryService.process(request);

    assertEquals(dataRoot.getCarriers().get("CC0"), dataRoot.getOrders().get("O0").getCarrierRef());
    assertEquals(null, dataRoot.getOrders().get("O10").getCarrierRef());

    assertEquals(true, dataRoot.getOrders().get("O0").isFulfilled());
    assertEquals(false, dataRoot.getOrders().get("O10").isFulfilled());
  }

  @Test
  public void checkIfCustomerUpdated() {
    DeliveryRequest request = new DeliveryRequest("W0", "CC0");
    DeliveryResponse response = deliveryService.process(request);

    assertNotNull(dataRoot.getOrders().get("O0").getItems().get(0).getDeliveryDate());
    assertEquals(dataRoot.getOrders().get("O0").getItems().get(0).getAmount(), dataRoot.getCustomers().get("C0").getBalance());
  }

}
