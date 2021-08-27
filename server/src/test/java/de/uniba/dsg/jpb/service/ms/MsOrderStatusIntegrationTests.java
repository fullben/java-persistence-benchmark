package de.uniba.dsg.jpb.service.ms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.DataNotFoundException;
import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusResponse;
import java.util.Comparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MsOrderStatusIntegrationTests extends MicroStreamServiceTest {

  private DataManager dataManager;
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
    populateStorage(new JpaDataGenerator(1, 1, 10, 10, 1_000, new BCryptPasswordEncoder()));
    dataManager = dataManager();
    request = new OrderStatusRequest();

    dataManager.read(
        (root) -> {
          WarehouseData warehouse = root.findAllWarehouses().get(0);
          warehouseId = warehouse.getId();
          DistrictData district = warehouse.getDistricts().get(0);
          districtId = district.getId();

          OrderData order =
              district.getOrders().stream()
                  .max(Comparator.comparing(OrderData::getEntryDate))
                  .orElseThrow(IllegalStateException::new);
          orderId = order.getId();
          orderItemCount = order.getItems().size();

          CustomerData customer = order.getCustomer();
          if (!customer.getDistrict().getId().equals(districtId)) {
            throw new IllegalStateException();
          }
          customerId = customer.getId();
          customerEmail = customer.getEmail();
          customerFirst = customer.getFirstName();
          customerMiddle = customer.getMiddleName();
          customerLast = customer.getLastName();
          customerBalance = customer.getBalance();

          request.setWarehouseId(warehouse.getId());
          request.setDistrictId(warehouse.getDistricts().get(0).getId());
          request.setCustomerId(order.getCustomer().getId());
          request.setCustomerEmail(null);
        });
    dataManager = closeGivenCreateNewDataManager(dataManager);

    orderStatusService = new MsOrderStatusService(dataManager);
  }

  @Test
  public void processingFailsWithMissingCustomerIdAndEmail() {
    request.setCustomerId(null);
    request.setCustomerEmail(null);

    assertThrows(DataNotFoundException.class, () -> orderStatusService.process(request));
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
    dataManager.close();
    clearStorage();
  }
}
