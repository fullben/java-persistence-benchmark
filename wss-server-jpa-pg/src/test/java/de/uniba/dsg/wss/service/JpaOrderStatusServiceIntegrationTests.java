package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uniba.dsg.wss.data.access.CarrierRepository;
import de.uniba.dsg.wss.data.access.CustomerRepository;
import de.uniba.dsg.wss.data.access.OrderRepository;
import de.uniba.dsg.wss.data.access.ProductRepository;
import de.uniba.dsg.wss.data.access.WarehouseRepository;
import de.uniba.dsg.wss.data.gen.JpaDataConverter;
import de.uniba.dsg.wss.data.gen.TestDataGenerator;
import de.uniba.dsg.wss.data.model.CustomerEntity;
import de.uniba.dsg.wss.data.model.OrderEntity;
import de.uniba.dsg.wss.data.model.WarehouseEntity;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;
import java.util.Comparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class JpaOrderStatusServiceIntegrationTests {

  @Autowired private WarehouseRepository warehouseRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private CarrierRepository carrierRepository;
  private JpaOrderStatusService orderStatusService;
  private OrderStatusRequest request;
  private OrderEntity order;
  private CustomerEntity customer;

  @BeforeEach
  public void setUp() {
    TestDataGenerator generator = new TestDataGenerator();
    generator.generate();
    JpaDataConverter converter = new JpaDataConverter();
    converter.convert(generator);

    productRepository.saveAll(converter.getProducts());
    carrierRepository.saveAll(converter.getCarriers());
    warehouseRepository.saveAll(converter.getWarehouses());

    order =
        converter.getWarehouses().get(0).getDistricts().get(0).getOrders().stream()
            .max(Comparator.comparing(OrderEntity::getEntryDate))
            .orElseThrow(IllegalStateException::new);
    customer = order.getCustomer();

    WarehouseEntity warehouse = warehouseRepository.findAll().get(0);
    request = new OrderStatusRequest();
    request.setWarehouseId(warehouse.getId());
    request.setDistrictId(warehouse.getDistricts().get(0).getId());
    request.setCustomerId(customer.getId());
    request.setCustomerEmail(null);

    orderStatusService = new JpaOrderStatusService(customerRepository, orderRepository);
  }

  @Test
  public void processingFailsWithMissingCustomerIdAndEmail() {
    request.setCustomerId(null);
    request.setCustomerEmail(null);

    assertThrows(IllegalArgumentException.class, () -> orderStatusService.process(request));
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerIdIsProvided() {
    request.setCustomerId(customer.getId());
    request.setCustomerEmail(null);

    OrderStatusResponse res = orderStatusService.process(request);

    assertEquals(customer.getDistrict().getWarehouse().getId(), res.getWarehouseId());
    assertEquals(customer.getDistrict().getId(), res.getDistrictId());
    assertEquals(customer.getId(), res.getCustomerId());
    assertEquals(customer.getFirstName(), res.getCustomerFirstName());
    assertEquals(customer.getMiddleName(), res.getCustomerMiddleName());
    assertEquals(customer.getLastName(), res.getCustomerLastName());
    assertEquals(customer.getBalance(), res.getCustomerBalance());
    assertEquals(order.getId(), res.getOrderId());
    assertEquals(order.getItemCount(), res.getItemStatus().size());
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerEmailIsProvided() {
    request.setCustomerId(null);
    request.setCustomerEmail(customer.getEmail());

    OrderStatusResponse res = orderStatusService.process(request);

    assertEquals(customer.getDistrict().getWarehouse().getId(), res.getWarehouseId());
    assertEquals(customer.getDistrict().getId(), res.getDistrictId());
    assertEquals(customer.getId(), res.getCustomerId());
    assertEquals(customer.getFirstName(), res.getCustomerFirstName());
    assertEquals(customer.getMiddleName(), res.getCustomerMiddleName());
    assertEquals(customer.getLastName(), res.getCustomerLastName());
    assertEquals(customer.getBalance(), res.getCustomerBalance());
    assertEquals(order.getId(), res.getOrderId());
    assertEquals(order.getItemCount(), res.getItemStatus().size());
  }

  @AfterEach
  public void tearDown() {
    warehouseRepository.deleteAll();
    productRepository.deleteAll();
    carrierRepository.deleteAll();
  }
}
