package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.wss.service.ms.MsOrderStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MsOrderStatusIntegrationTests extends MicroStreamServiceTest {

  @Autowired
  private MsOrderStatusService orderStatusService;
  @Autowired
  private MsDataRoot msDataRoot;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
  }

  @Test
  public void processingFailsWithMissingCustomerIdAndEmail() {
    OrderStatusRequest request = new OrderStatusRequest("W0","D0",null,null);

    assertThrows(IllegalStateException.class, () -> orderStatusService.process(request));
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerIdIsProvided() {
    // most recent order is O10
    OrderStatusRequest request = new OrderStatusRequest("W0","D0","C0",null);

    OrderStatusResponse res = orderStatusService.process(request);

    assertEquals("W0", res.getWarehouseId());
    assertEquals("D0", res.getDistrictId());
    assertEquals("C0", res.getCustomerId());
    assertEquals("C0-first", res.getCustomerFirstName());
    assertEquals("C0-middle", res.getCustomerMiddleName());
    assertEquals("C0-last", res.getCustomerLastName());
    assertEquals(msDataRoot.getCustomers().get("C0").getBalance(), res.getCustomerBalance());
    assertEquals("O10", res.getOrderId());
    assertEquals(1, res.getItemStatus().size());
  }

}
