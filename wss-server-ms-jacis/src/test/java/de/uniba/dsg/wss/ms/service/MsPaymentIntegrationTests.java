package de.uniba.dsg.wss.ms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.wss.ms.data.gen.MsDataWriter;
import de.uniba.dsg.wss.ms.data.model.CustomerData;
import de.uniba.dsg.wss.ms.data.model.DistrictData;
import de.uniba.dsg.wss.ms.data.model.PaymentData;
import de.uniba.dsg.wss.ms.data.model.WarehouseData;
import java.util.stream.Collectors;
import org.jacis.container.JacisContainer;
import org.jacis.store.JacisStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MsPaymentIntegrationTests extends MicroStreamServiceTest {

  @Autowired private JacisContainer container;
  @Autowired private JacisStore<String, WarehouseData> warehouseStore;
  @Autowired private JacisStore<String, DistrictData> districtStore;
  @Autowired private JacisStore<String, CustomerData> customerStore;
  @Autowired private JacisStore<String, PaymentData> paymentStore;
  @Autowired private MsDataWriter dataWriter;
  private MsPaymentService paymentService;
  private PaymentRequest request;
  private String warehouseId;
  private double warehouseBalance;
  private String districtId;
  private double districtBalance;
  private String customerId;
  private String customerEmail;
  private double customerPayment;
  private double customerBalance;
  private String customerCredit;
  private double customerCreditLimit;
  private double customerDiscount;
  private long paymentCount;

  @BeforeEach
  public void setUp() {
    populateStorage(
        new DataGenerator(1, 1, 10, 10, 1_000, new BCryptPasswordEncoder()), dataWriter);
    request = new PaymentRequest();

    WarehouseData warehouse = warehouseStore.getAllReadOnly().get(0);
    warehouseId = warehouse.getId();
    warehouseBalance = warehouse.getYearToDateBalance();
    DistrictData district =
        districtStore
            .streamReadOnly(d -> d.getWarehouseId().equals(warehouse.getId()))
            .collect(Collectors.toList())
            .get(0);
    districtId = district.getId();
    districtBalance = district.getYearToDateBalance();

    CustomerData customer =
        customerStore.getAllReadOnly(c -> c.getDistrictId().equals(districtId)).get(0);
    customerId = customer.getId();
    customerEmail = customer.getEmail();
    customerPayment = customer.getYearToDatePayment();
    customerBalance = customer.getBalance();
    customerCredit = customer.getCredit();
    customerCreditLimit = customer.getCreditLimit();
    customerDiscount = customer.getDiscount();

    paymentCount = customer.getPaymentCount();
    if (paymentCount == 0) {
      // Customer is required to have at least one payment prior to test
      throw new IllegalStateException();
    }

    request = new PaymentRequest();
    request.setWarehouseId(warehouse.getId());
    request.setDistrictId(districtId);
    request.setCustomerId(customer.getId());
    request.setCustomerEmail(null);
    request.setAmount(500);

    paymentService =
        new MsPaymentService(container, warehouseStore, districtStore, customerStore, paymentStore);
  }

  @Test
  public void processingFailsWithMissingCustomerIdAndEmail() {
    request.setCustomerId(null);
    request.setCustomerEmail(null);

    assertThrows(IllegalStateException.class, () -> paymentService.process(request));
  }

  @Test
  public void processingPersistsNewPayment() {
    PaymentResponse res = paymentService.process(request);

    CustomerData customer = customerStore.getReadOnly(customerId);
    assertEquals(paymentCount + 1, customer.getPaymentCount());
    assertTrue(paymentStore.streamReadOnly().anyMatch(p -> p.getId().equals(res.getPaymentId())));
  }

  @Test
  public void processingUpdatesWarehouseAndDistrict() {
    paymentService.process(request);

    WarehouseData warehouse = warehouseStore.getReadOnly(warehouseId);
    assertEquals(warehouseBalance + request.getAmount(), warehouse.getYearToDateBalance());
    DistrictData district = districtStore.getReadOnly(districtId);
    assertEquals(districtBalance + request.getAmount(), district.getYearToDateBalance());
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerIdIsProvided() {
    request.setCustomerId(customerId);
    request.setCustomerEmail(null);

    PaymentResponse res = paymentService.process(request);

    WarehouseData updatedWarehouse = warehouseStore.getReadOnly(warehouseId);
    DistrictData updatedDistrict = districtStore.getReadOnly(districtId);
    CustomerData updatedCustomer = customerStore.getReadOnly(customerId);
    assertEquals(warehouseBalance + request.getAmount(), updatedWarehouse.getYearToDateBalance());
    assertEquals(districtBalance + request.getAmount(), updatedDistrict.getYearToDateBalance());
    assertEquals(customerPayment + request.getAmount(), updatedCustomer.getYearToDatePayment());

    assertEquals(warehouseId, res.getWarehouseId());
    assertEquals(districtId, res.getDistrictId());
    assertEquals(customerId, res.getCustomerId());
    assertEquals(customerBalance - request.getAmount(), res.getCustomerBalance());
    assertEquals(customerCredit, res.getCustomerCredit());
    assertEquals(customerCreditLimit, res.getCustomerCreditLimit());
    assertEquals(customerDiscount, res.getCustomerDiscount());
    assertEquals(request.getAmount(), res.getPaymentAmount());
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerEmailIsProvided() {
    request.setCustomerId(null);
    request.setCustomerEmail(customerEmail);

    PaymentResponse res = paymentService.process(request);

    WarehouseData updatedWarehouse = warehouseStore.getReadOnly(warehouseId);
    DistrictData updatedDistrict = districtStore.getReadOnly(districtId);
    CustomerData updatedCustomer = customerStore.getReadOnly(customerId);
    assertEquals(warehouseBalance + request.getAmount(), updatedWarehouse.getYearToDateBalance());
    assertEquals(districtBalance + request.getAmount(), updatedDistrict.getYearToDateBalance());
    assertEquals(customerPayment + request.getAmount(), updatedCustomer.getYearToDatePayment());

    assertEquals(warehouseId, res.getWarehouseId());
    assertEquals(districtId, res.getDistrictId());
    assertEquals(customerId, res.getCustomerId());
    assertEquals(customerBalance - request.getAmount(), res.getCustomerBalance());
    assertEquals(customerCredit, res.getCustomerCredit());
    assertEquals(customerCreditLimit, res.getCustomerCreditLimit());
    assertEquals(customerDiscount, res.getCustomerDiscount());
    assertEquals(request.getAmount(), res.getPaymentAmount());
  }

  @AfterEach
  public void tearDown() {
    container.clearAllStores();
  }
}
