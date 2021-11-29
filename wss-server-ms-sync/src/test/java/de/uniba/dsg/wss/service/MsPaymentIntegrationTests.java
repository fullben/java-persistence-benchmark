package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MsPaymentIntegrationTests extends MicroStreamServiceTest {

  @Autowired private MsPaymentService paymentService;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
  }

  @Test
  public void processingFailsWithMissingCustomerIdAndEmail() {
    PaymentRequest request = new PaymentRequest("W0", "D0", null, null, 0.9);
    request.setCustomerId(null);
    request.setCustomerEmail(null);

    assertThrows(IllegalStateException.class, () -> paymentService.process(request));
  }

  @Test
  public void processingFailsWithWrongId() {
    PaymentRequest request = new PaymentRequest("W0", "D0", "X0", null, 0.9);
    request.setCustomerId(null);
    request.setCustomerEmail(null);

    assertThrows(IllegalStateException.class, () -> paymentService.process(request));
  }

  @Test
  public void processingPersistsNewPayment() {
    String customerId = "C0";
    double amount = 12.45;
    int paymentCount = msDataRoot.getCustomers().get(customerId).getPaymentCount();
    assertEquals(1, paymentCount);
    PaymentRequest request = new PaymentRequest("W0", "D0", customerId, null, amount);

    int iterations = 5;
    PaymentResponse res = null;
    for (int i = 0; i < iterations; i++) {
      res = paymentService.process(request);
    }

    CustomerData customer = msDataRoot.getCustomers().get(customerId);
    assertEquals(amount * (-1) * iterations, customer.getBalance());
    assertEquals(amount * iterations, customer.getYearToDatePayment());
    PaymentResponse finalRes = res;
    assertTrue(
        customer.getPaymentRefs().stream()
            .anyMatch(p -> p.getId().equals(finalRes.getPaymentId())));
    assertEquals(iterations + 1, customer.getPaymentCount());
    assertEquals(iterations + 1, customer.getPaymentRefs().size());
  }

  @Test
  public void processingUpdatesWarehouseAndDistrict() {
    String customerId = "C0";
    double amount = 12.45;
    CustomerData customer = msDataRoot.getCustomers().get(customerId);
    int paymentCount = msDataRoot.getCustomers().get(customerId).getPaymentCount();
    PaymentRequest request = new PaymentRequest("W0", "D0", customerId, null, amount);

    paymentService.process(request);

    WarehouseData warehouse = msDataRoot.getWarehouses().get(request.getWarehouseId());
    assertEquals(request.getAmount(), warehouse.getYearToDateBalance());

    DistrictData district = warehouse.getDistricts().get(request.getDistrictId());
    assertEquals(request.getAmount(), district.getYearToDateBalance());
  }
}
