package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uniba.dsg.wss.data.access.CarrierRepository;
import de.uniba.dsg.wss.data.access.CustomerRepository;
import de.uniba.dsg.wss.data.access.ProductRepository;
import de.uniba.dsg.wss.data.access.WarehouseRepository;
import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.data.gen.JpaDataConverter;
import de.uniba.dsg.wss.data.model.CustomerEntity;
import de.uniba.dsg.wss.data.model.DistrictEntity;
import de.uniba.dsg.wss.data.model.WarehouseEntity;
import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.wss.data.access.DistrictRepository;
import de.uniba.dsg.wss.data.access.OrderRepository;
import de.uniba.dsg.wss.data.access.PaymentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DataJpaTest
public class JpaPaymentServiceIntegrationTests {

  @Autowired private WarehouseRepository warehouseRepository;
  @Autowired private DistrictRepository districtRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private CarrierRepository carrierRepository;
  @Autowired private PaymentRepository paymentRepository;
  private JpaPaymentService paymentService;
  private PaymentRequest request;
  private WarehouseEntity warehouse;
  private double warehouseBalance;
  private double districtBalance;
  private CustomerEntity customer;
  private double customerPayment;
  private double customerBalance;
  private String customerCredit;
  private double customerCreditLimit;
  private double customerDiscount;
  private long paymentCount;

  @BeforeEach
  public void setUp() {
    DataGenerator generator = new DataGenerator(1, 1, 10, 10, 1_000, new BCryptPasswordEncoder());
    generator.generate();
    JpaDataConverter converter = new JpaDataConverter();
    converter.convert(generator);

    productRepository.saveAll(converter.getProducts());
    carrierRepository.saveAll(converter.getCarriers());
    warehouseRepository.saveAll(converter.getWarehouses());

    warehouse = warehouseRepository.findAll().get(0);
    warehouseBalance = warehouse.getYearToDateBalance();
    DistrictEntity district = warehouse.getDistricts().get(0);
    districtBalance = district.getYearToDateBalance();
    customer = district.getCustomers().get(0);
    customerPayment = customer.getYearToDatePayment();
    customerBalance = customer.getBalance();
    customerCredit = customer.getCredit();
    customerCreditLimit = customer.getCreditLimit();
    customerDiscount = customer.getDiscount();

    paymentCount = paymentRepository.count();

    request = new PaymentRequest();
    request.setWarehouseId(warehouse.getId());
    request.setDistrictId(warehouse.getDistricts().get(0).getId());
    request.setCustomerId(customer.getId());
    request.setCustomerEmail(null);
    request.setAmount(500);

    paymentService =
        new JpaPaymentService(
            warehouseRepository, districtRepository, customerRepository, paymentRepository);
  }

  @Test
  public void processingFailsWithMissingCustomerIdAndEmail() {
    request.setCustomerId(null);
    request.setCustomerEmail(null);

    assertThrows(IllegalArgumentException.class, () -> paymentService.process(request));
  }

  @Test
  public void processingPersistsNewPayment() {
    PaymentResponse res = paymentService.process(request);

    assertEquals(paymentCount + 1, paymentRepository.count());
    assertNotNull(paymentRepository.findById(res.getPaymentId()).get());
  }

  @Test
  public void processingUpdatesWarehouseAndDistrict() {
    paymentService.process(request);

    WarehouseEntity warehouse = warehouseRepository.getById(request.getWarehouseId());
    assertEquals(warehouseBalance + request.getAmount(), warehouse.getYearToDateBalance());
    DistrictEntity district = districtRepository.getById(request.getDistrictId());
    assertEquals(districtBalance + request.getAmount(), district.getYearToDateBalance());
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerIdIsProvided() {
    request.setCustomerId(customer.getId());
    request.setCustomerEmail(null);

    PaymentResponse res = paymentService.process(request);

    WarehouseEntity updatedWarehouse = warehouseRepository.getById(warehouse.getId());
    DistrictEntity updatedDistrict = warehouse.getDistricts().get(0);
    CustomerEntity updatedCustomer = customerRepository.getById(customer.getId());
    assertEquals(warehouseBalance + request.getAmount(), updatedWarehouse.getYearToDateBalance());
    assertEquals(districtBalance + request.getAmount(), updatedDistrict.getYearToDateBalance());
    assertEquals(customerPayment + request.getAmount(), updatedCustomer.getYearToDatePayment());

    assertEquals(customer.getDistrict().getWarehouse().getId(), res.getWarehouseId());
    assertEquals(customer.getDistrict().getId(), res.getDistrictId());
    assertEquals(customer.getId(), res.getCustomerId());
    assertEquals(customerBalance - request.getAmount(), res.getCustomerBalance());
    assertEquals(customerCredit, res.getCustomerCredit());
    assertEquals(customerCreditLimit, res.getCustomerCreditLimit());
    assertEquals(customerDiscount, res.getCustomerDiscount());
    assertEquals(request.getAmount(), res.getPaymentAmount());
  }

  @Test
  public void processingReturnsExpectedValuesIfCustomerEmailIsProvided() {
    request.setCustomerId(null);
    request.setCustomerEmail(customer.getEmail());

    PaymentResponse res = paymentService.process(request);

    WarehouseEntity updatedWarehouse = warehouseRepository.getById(warehouse.getId());
    DistrictEntity updatedDistrict = warehouse.getDistricts().get(0);
    CustomerEntity updatedCustomer = customerRepository.getById(customer.getId());
    assertEquals(warehouseBalance + request.getAmount(), updatedWarehouse.getYearToDateBalance());
    assertEquals(districtBalance + request.getAmount(), updatedDistrict.getYearToDateBalance());
    assertEquals(customerPayment + request.getAmount(), updatedCustomer.getYearToDatePayment());

    assertEquals(customer.getDistrict().getWarehouse().getId(), res.getWarehouseId());
    assertEquals(customer.getDistrict().getId(), res.getDistrictId());
    assertEquals(customer.getId(), res.getCustomerId());
    assertEquals(customerBalance - request.getAmount(), res.getCustomerBalance());
    assertEquals(customerCredit, res.getCustomerCredit());
    assertEquals(customerCreditLimit, res.getCustomerCreditLimit());
    assertEquals(customerDiscount, res.getCustomerDiscount());
    assertEquals(request.getAmount(), res.getPaymentAmount());
  }

  @AfterEach
  public void tearDown() {
    warehouseRepository.deleteAll();
    productRepository.deleteAll();
    carrierRepository.deleteAll();
  }
}
