package de.uniba.dsg.jpb.service.ms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.data.gen.ms.MsDataWriter;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusResponse;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.jacis.container.JacisContainer;
import org.jacis.store.JacisStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MsOrderStatusIntegrationTests extends MicroStreamServiceTest {

  @Autowired private JacisContainer container;
  @Autowired private JacisStore<String, WarehouseData> warehouseStore;
  @Autowired private JacisStore<String, DistrictData> districtStore;
  @Autowired private JacisStore<String, CustomerData> customerStore;
  @Autowired private JacisStore<String, OrderData> orderStore;
  @Autowired private JacisStore<String, OrderItemData> orderItemStore;
  @Autowired private MsDataWriter dataWriter;
  private MsOrderStatusService orderStatusService;
  private OrderStatusRequest request;
  private String warehouseId;
  private String districtId;
  private String orderId;
  private int orderItemCount;
  private String customerId;
  private String customerEmail;
  private String customerFirst;
  private String customerMiddle;
  private String customerLast;
  private double customerBalance;

  @BeforeEach
  public void setUp() {
    populateStorage(
        new JpaDataGenerator(1, 1, 10, 10, 1_000, new BCryptPasswordEncoder()), dataWriter);
    request = new OrderStatusRequest();

    WarehouseData warehouse = warehouseStore.getAllReadOnly().get(0);
    warehouseId = warehouse.getId();
    DistrictData district =
        districtStore
            .streamReadOnly(d -> d.getWarehouseId().equals(warehouse.getId()))
            .collect(Collectors.toList())
            .get(0);
    districtId = district.getId();

    OrderData order =
        orderStore
            .streamReadOnly(o -> o.getDistrictId().equals(districtId))
            .max(Comparator.comparing(OrderData::getEntryDate))
            .orElseThrow(IllegalStateException::new);
    orderId = order.getId();
    orderItemCount = order.getItemCount();

    CustomerData customer =
        customerStore.getAllReadOnly(c -> c.getId().equals(order.getCustomerId())).get(0);
    if (!customer.getDistrictId().equals(districtId)) {
      throw new IllegalStateException();
    }
    customerId = customer.getId();
    customerEmail = customer.getEmail();
    customerFirst = customer.getFirstName();
    customerMiddle = customer.getMiddleName();
    customerLast = customer.getLastName();
    customerBalance = customer.getBalance();

    request.setWarehouseId(warehouse.getId());
    request.setDistrictId(districtId);
    request.setCustomerId(customerId);
    request.setCustomerEmail(null);

    orderStatusService =
        new MsOrderStatusService(
            warehouseStore, districtStore, customerStore, orderStore, orderItemStore);
  }

  @Test
  public void processingFailsWithMissingCustomerIdAndEmail() {
    request.setCustomerId(null);
    request.setCustomerEmail(null);

    assertThrows(IllegalStateException.class, () -> orderStatusService.process(request));
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerIdIsProvided() {
    request.setCustomerId(customerId);
    request.setCustomerEmail(null);

    OrderStatusResponse res = orderStatusService.process(request);

    assertEquals(warehouseId, res.getWarehouseId());
    assertEquals(districtId, res.getDistrictId());
    assertEquals(customerId, res.getCustomerId());
    assertEquals(customerFirst, res.getCustomerFirstName());
    assertEquals(customerMiddle, res.getCustomerMiddleName());
    assertEquals(customerLast, res.getCustomerLastName());
    assertEquals(customerBalance, res.getCustomerBalance());
    assertEquals(orderId, res.getOrderId());
    assertEquals(orderItemCount, res.getItemStatus().size());
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerEmailIsProvided() {
    request.setCustomerId(null);
    request.setCustomerEmail(customerEmail);

    OrderStatusResponse res = orderStatusService.process(request);

    assertEquals(warehouseId, res.getWarehouseId());
    assertEquals(districtId, res.getDistrictId());
    assertEquals(customerId, res.getCustomerId());
    assertEquals(customerFirst, res.getCustomerFirstName());
    assertEquals(customerMiddle, res.getCustomerMiddleName());
    assertEquals(customerLast, res.getCustomerLastName());
    assertEquals(customerBalance, res.getCustomerBalance());
    assertEquals(orderId, res.getOrderId());
    assertEquals(orderItemCount, res.getItemStatus().size());
  }

  @AfterEach
  public void tearDown() {
    container.clearAllStores();
  }
}
