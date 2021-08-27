package de.uniba.dsg.jpb.service.ms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.DataNotFoundException;
import de.uniba.dsg.jpb.data.access.ms.Find;
import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MsPaymentServiceIntegrationTests extends MicroStreamServiceTest {

  private DataManager dataManager;
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
    populateStorage(new JpaDataGenerator(1, 1, 10, 10, 1_000, new BCryptPasswordEncoder()));
    dataManager = dataManager();
    request = new PaymentRequest();

    dataManager.read(
        (root) -> {
          WarehouseData warehouse = root.findAllWarehouses().get(0);
          warehouseId = warehouse.getId();
          warehouseBalance = warehouse.getYearToDateBalance();
          DistrictData district = warehouse.getDistricts().get(0);
          districtId = district.getId();
          districtBalance = district.getYearToDateBalance();
          CustomerData customer = district.getCustomers().get(0);
          customerId = customer.getId();
          customerEmail = customer.getEmail();
          customerPayment = customer.getYearToDatePayment();
          customerBalance = customer.getBalance();
          customerCredit = customer.getCredit();
          customerCreditLimit = customer.getCreditLimit();
          customerDiscount = customer.getDiscount();

          paymentCount = customer.getPayments().size();

          request = new PaymentRequest();
          request.setWarehouseId(warehouse.getId());
          request.setDistrictId(warehouse.getDistricts().get(0).getId());
          request.setCustomerId(customer.getId());
          request.setCustomerEmail(null);
          request.setAmount(500);
        });

    paymentService = new MsPaymentService(dataManager);
  }

  @Test
  public void processingFailsWithMissingCustomerIdAndEmail() {
    request.setCustomerId(null);
    request.setCustomerEmail(null);

    assertThrows(DataNotFoundException.class, () -> paymentService.process(request));
  }

  @Test
  public void processingPersistsNewPayment() {
    PaymentResponse res = paymentService.process(request);

    dataManager = closeGivenCreateNewDataManager(dataManager);
    dataManager.read(
        (root) -> {
          WarehouseData warehouse = Find.warehouseById(warehouseId, root.findAllWarehouses());
          DistrictData district = Find.districtById(districtId, warehouse);
          CustomerData customer = Find.customerById(customerId, district);
          assertEquals(paymentCount + 1, customer.getPayments().size());
          assertNotNull(
              customer.getPayments().stream()
                  .filter(p -> p.getId().equals(res.getPaymentId()))
                  .findAny()
                  .orElse(null));
        });
  }

  @Test
  public void processingUpdatesWarehouseAndDistrict() {
    paymentService.process(request);

    dataManager = closeGivenCreateNewDataManager(dataManager);
    dataManager.read(
        (root) -> {
          WarehouseData warehouse = Find.warehouseById(warehouseId, root.findAllWarehouses());
          assertEquals(warehouseBalance + request.getAmount(), warehouse.getYearToDateBalance());
          DistrictData district = Find.districtById(districtId, warehouse);
          assertEquals(districtBalance + request.getAmount(), district.getYearToDateBalance());
        });
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerIdIsProvided() {
    request.setCustomerId(customerId);
    request.setCustomerEmail(null);

    PaymentResponse res = paymentService.process(request);

    dataManager = closeGivenCreateNewDataManager(dataManager);
    dataManager.read(
        (root) -> {
          WarehouseData updatedWarehouse =
              Find.warehouseById(warehouseId, root.findAllWarehouses());
          DistrictData updatedDistrict = Find.districtById(districtId, updatedWarehouse);
          CustomerData updatedCustomer = Find.customerById(customerId, updatedDistrict);
          assertEquals(
              warehouseBalance + request.getAmount(), updatedWarehouse.getYearToDateBalance());
          assertEquals(
              districtBalance + request.getAmount(), updatedDistrict.getYearToDateBalance());
          assertEquals(
              customerPayment + request.getAmount(), updatedCustomer.getYearToDatePayment());

          assertEquals(warehouseId, res.getWarehouseId());
          assertEquals(districtId, res.getDistrictId());
          assertEquals(customerId, res.getCustomerId());
          assertEquals(customerBalance - request.getAmount(), res.getCustomerBalance());
          assertEquals(customerCredit, res.getCustomerCredit());
          assertEquals(customerCreditLimit, res.getCustomerCreditLimit());
          assertEquals(customerDiscount, res.getCustomerDiscount());
          assertEquals(request.getAmount(), res.getPaymentAmount());
        });
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerEmailIsProvided() {
    request.setCustomerId(null);
    request.setCustomerEmail(customerEmail);

    PaymentResponse res = paymentService.process(request);

    dataManager = closeGivenCreateNewDataManager(dataManager);
    dataManager.read(
        (root) -> {
          WarehouseData updatedWarehouse =
              Find.warehouseById(warehouseId, root.findAllWarehouses());
          DistrictData updatedDistrict = Find.districtById(districtId, updatedWarehouse);
          CustomerData updatedCustomer = Find.customerByEmail(customerEmail, updatedDistrict);
          assertEquals(
              warehouseBalance + request.getAmount(), updatedWarehouse.getYearToDateBalance());
          assertEquals(
              districtBalance + request.getAmount(), updatedDistrict.getYearToDateBalance());
          assertEquals(
              customerPayment + request.getAmount(), updatedCustomer.getYearToDatePayment());

          assertEquals(warehouseId, res.getWarehouseId());
          assertEquals(districtId, res.getDistrictId());
          assertEquals(customerId, res.getCustomerId());
          assertEquals(customerBalance - request.getAmount(), res.getCustomerBalance());
          assertEquals(customerCredit, res.getCustomerCredit());
          assertEquals(customerCreditLimit, res.getCustomerCreditLimit());
          assertEquals(customerDiscount, res.getCustomerDiscount());
          assertEquals(request.getAmount(), res.getPaymentAmount());
        });
  }

  @AfterEach
  public void tearDown() {
    dataManager.close();
    clearStorage();
  }
}
