package de.uniba.dsg.wss.service.jpa;

import de.uniba.dsg.wss.data.access.jpa.CustomerRepository;
import de.uniba.dsg.wss.data.access.jpa.DistrictRepository;
import de.uniba.dsg.wss.data.access.jpa.PaymentRepository;
import de.uniba.dsg.wss.data.access.jpa.WarehouseRepository;
import de.uniba.dsg.wss.data.model.jpa.CustomerEntity;
import de.uniba.dsg.wss.data.model.jpa.DistrictEntity;
import de.uniba.dsg.wss.data.model.jpa.PaymentEntity;
import de.uniba.dsg.wss.data.model.jpa.WarehouseEntity;
import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.wss.service.PaymentService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaPaymentService extends PaymentService {

  private final WarehouseRepository warehouseRepository;
  private final DistrictRepository districtRepository;
  private final CustomerRepository customerRepository;
  private final PaymentRepository paymentRepository;

  @Autowired
  public JpaPaymentService(
      WarehouseRepository warehouseRepository,
      DistrictRepository districtRepository,
      CustomerRepository customerRepository,
      PaymentRepository paymentRepository) {
    this.warehouseRepository = warehouseRepository;
    this.districtRepository = districtRepository;
    this.customerRepository = customerRepository;
    this.paymentRepository = paymentRepository;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Override
  public PaymentResponse process(PaymentRequest req) {
    // Fetch warehouse, district, and customer (either by id or email)
    WarehouseEntity warehouse = warehouseRepository.getById(req.getWarehouseId());
    DistrictEntity district = districtRepository.getById(req.getDistrictId());
    String customerId = req.getCustomerId();
    CustomerEntity customer;
    if (customerId == null) {
      customer =
          customerRepository
              .findByEmail(req.getCustomerEmail())
              .orElseThrow(IllegalArgumentException::new);
    } else {
      customer = customerRepository.getById(customerId);
    }

    // Update warehouse and district year to data balance
    warehouse.setYearToDateBalance(warehouse.getYearToDateBalance() + req.getAmount());
    warehouse = warehouseRepository.save(warehouse);
    district.setYearToDateBalance(district.getYearToDateBalance() + req.getAmount());
    district = districtRepository.save(district);

    // Update customer balance, year to data payment, and payment count
    customer.setBalance(customer.getBalance() - req.getAmount());
    customer.setYearToDatePayment(customer.getYearToDatePayment() + req.getAmount());
    customer.setPaymentCount(customer.getPaymentCount() + 1);
    // Update customer data if the customer has bad credit
    if (customerHasBadCredit(customer.getCredit())) {
      customer.setData(
          buildNewCustomerData(
              customer.getId(),
              customer.getDistrict().getId(),
              customer.getDistrict().getWarehouse().getId(),
              req.getAmount(),
              customer.getData()));
    }
    customer = customerRepository.save(customer);

    // Create a new entry for this payment
    PaymentEntity payment = new PaymentEntity();
    payment.setCustomer(customer);
    payment.setDate(LocalDateTime.now());
    payment.setDistrict(district);
    payment.setData(buildPaymentData(warehouse.getName(), district.getName()));
    payment.setAmount(req.getAmount());
    payment = paymentRepository.save(payment);

    PaymentResponse res = new PaymentResponse(req);
    res.setCustomerId(customer.getId());
    res.setPaymentId(payment.getId());
    res.setCustomerCredit(customer.getCredit());
    res.setCustomerCreditLimit(customer.getCreditLimit());
    res.setCustomerDiscount(customer.getDiscount());
    res.setCustomerBalance(customer.getBalance());
    return res;
  }
}
