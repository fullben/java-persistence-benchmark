package de.uniba.dsg.jpb.service.ms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.jpb.data.transfer.messages.DeliveryResponse;
import one.microstream.persistence.types.Storer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MsDeliveryServiceIntegrationTests extends MicroStreamServiceTest {

  private DataManager dataManager;
  private MsDeliveryService deliveryService;
  private DeliveryRequest request;
  private String orderId;

  @BeforeEach
  public void setUp() {
    populateStorage(new JpaDataGenerator(1, 1, 1, 1, 1_000, new BCryptPasswordEncoder()));
    dataManager = dataManager();
    request = new DeliveryRequest();

    dataManager.write(
        (root, storageManager) -> {
          WarehouseData warehouse = root.findAllWarehouses().get(0);
          request.setWarehouseId(warehouse.getId());
          request.setCarrierId(root.findAllCarriers().get(0).getId());

          // Ensure that single order is not fulfilled
          OrderData order = warehouse.getDistricts().get(0).getOrders().get(0);
          orderId = order.getId();
          order.setCarrier(null);
          order.setFulfilled(false);
          order.getItems().forEach(i -> i.setDeliveryDate(null));

          Storer storer = storageManager.createEagerStorer();
          storer.store(order);
          storer.commit();
        });
    dataManager = closeGivenCreateNewDataManager(dataManager);

    deliveryService = new MsDeliveryService(dataManager);
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

    dataManager = closeGivenCreateNewDataManager(dataManager);
    dataManager.read(
        (root) -> {
          OrderData order =
              root.findAllWarehouses().get(0).getDistricts().get(0).getOrders().stream()
                  .filter(o -> o.getId().equals(orderId))
                  .findAny()
                  .orElseThrow(IllegalStateException::new);
          assertTrue(order.isFulfilled());
          assertNotNull(order.getCarrier());
          for (OrderItemData item : order.getItems()) {
            assertNotNull(item.getDeliveryDate());
          }
        });
  }

  @AfterEach
  public void tearDown() {
    dataManager.close();
    clearStorage();
  }
}
