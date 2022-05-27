package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.uniba.dsg.wss.data.access.CarrierRepository;
import de.uniba.dsg.wss.data.access.CustomerRepository;
import de.uniba.dsg.wss.data.access.DistrictRepository;
import de.uniba.dsg.wss.data.access.OrderRepository;
import de.uniba.dsg.wss.data.access.ProductRepository;
import de.uniba.dsg.wss.data.access.WarehouseRepository;
import de.uniba.dsg.wss.data.gen.DataModel;
import de.uniba.dsg.wss.data.gen.JpaDataConverter;
import de.uniba.dsg.wss.data.gen.TestDataGenerator;
import de.uniba.dsg.wss.data.model.CarrierEntity;
import de.uniba.dsg.wss.data.model.EmployeeEntity;
import de.uniba.dsg.wss.data.model.OrderEntity;
import de.uniba.dsg.wss.data.model.OrderItemEntity;
import de.uniba.dsg.wss.data.model.ProductEntity;
import de.uniba.dsg.wss.data.model.WarehouseEntity;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class JpaDeliveryServiceIntegrationTests {

  @Autowired private WarehouseRepository warehouseRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private DistrictRepository districtRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private CarrierRepository carrierRepository;
  private JpaDeliveryService deliveryService;
  private DeliveryRequest request;
  private List<String> orderIds = List.of("O0", "O5", "O10", "O15");

  @BeforeEach
  public void setUp() {
    JpaDataConverter converter = new JpaDataConverter();
    DataModel<ProductEntity, WarehouseEntity, EmployeeEntity, CarrierEntity> model =
        converter.convert(new TestDataGenerator().generate());

    productRepository.saveAll(model.getProducts());
    carrierRepository.saveAll(model.getCarriers());
    warehouseRepository.saveAll(model.getWarehouses());

    // Ensure that single order is not fulfilled
    // O0 and O10 belong to D0, O5 and O15 belong to D5
    List<String> orderIds = List.of("O0", "O5", "O10", "O15");
    for (String id : orderIds) {
      OrderEntity order = orderRepository.getById(id);
      order.setCarrier(null);
      order.setFulfilled(false);
      order.getItems().forEach(i -> i.setDeliveryDate(null));
      orderRepository.save(order);
    }

    request = new DeliveryRequest();
    request.setWarehouseId("W0");
    request.setCarrierId("CC0");

    deliveryService =
        new JpaDeliveryService(
            districtRepository, orderRepository, customerRepository, carrierRepository);
  }

  @Test
  public void deliveryProcessingReturnsExpectedValues() {
    DeliveryResponse res = deliveryService.process(request);

    assertEquals(request.getWarehouseId(), res.getWarehouseId());
    assertEquals(request.getCarrierId(), res.getCarrierId());
  }

  @Test
  public void ordersAreUpdatedByDeliveryRequest() {
    deliveryService.process(request);

    List<OrderEntity> orders = orderRepository.findAllById(orderIds);
    int fulfilledOrders = 0;

    // for each district D0 and D5 a single order has to be fulfilled (the oldest one)
    for (OrderEntity order : orders) {
      if (order.isFulfilled()) {
        fulfilledOrders++;
        assertNotNull(order.getCarrier());
        for (OrderItemEntity item : order.getItems()) {
          assertNotNull(item.getDeliveryDate());
        }
      }
    }

    assertEquals(2, fulfilledOrders);
  }

  @Test
  public void fullfillAllOrdersByDeliveryRequest() {
    deliveryService.process(request);
    deliveryService.process(request);

    List<OrderEntity> orders = orderRepository.findAllById(orderIds);
    int fulfilledOrders = 0;

    // for each district D0 and D5 a single order has to be fulfilled (the oldest one)
    for (OrderEntity order : orders) {
      if (order.isFulfilled()) {
        fulfilledOrders++;
        assertNotNull(order.getCarrier());
        for (OrderItemEntity item : order.getItems()) {
          assertNotNull(item.getDeliveryDate());
        }
      }
    }

    assertEquals(4, fulfilledOrders);
  }

  @AfterEach
  public void tearDown() {
    warehouseRepository.deleteAll();
    productRepository.deleteAll();
    carrierRepository.deleteAll();
  }
}
