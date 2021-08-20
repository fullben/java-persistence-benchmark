package de.uniba.dsg.jpb.service.jpa;

import de.uniba.dsg.jpb.data.access.jpa.CustomerRepository;
import de.uniba.dsg.jpb.data.access.jpa.DistrictRepository;
import de.uniba.dsg.jpb.data.access.jpa.PaymentRepository;
import de.uniba.dsg.jpb.data.access.jpa.WarehouseRepository;
import de.uniba.dsg.jpb.data.model.jpa.CustomerEntity;
import de.uniba.dsg.jpb.data.model.jpa.DistrictEntity;
import de.uniba.dsg.jpb.data.model.jpa.PaymentEntity;
import de.uniba.dsg.jpb.data.model.jpa.WarehouseEntity;
import de.uniba.dsg.jpb.messages.PaymentRequest;
import de.uniba.dsg.jpb.messages.PaymentResponse;
import de.uniba.dsg.jpb.service.PaymentService;
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
    WarehouseEntity warehouse = warehouseRepository.getById(req.getWarehouseId());
    DistrictEntity district = districtRepository.getById(req.getDistrictId());
    Long customerId = req.getCustomerId();
    CustomerEntity customer;
    if (customerId == null) {
      customer =
          customerRepository
              .findByEmail(req.getCustomerEmail())
              .orElseThrow(IllegalStateException::new);
    } else {
      customer = customerRepository.getById(customerId);
    }
    warehouse.setYearToDateBalance(warehouse.getYearToDateBalance() + req.getAmount());
    warehouse = warehouseRepository.save(warehouse);
    district.setYearToDateBalance(district.getYearToDateBalance() + req.getAmount());
    district = districtRepository.save(district);
    customer.setBalance(customer.getBalance() - req.getAmount());
    customer.setYearToDatePayment(customer.getYearToDatePayment() + req.getAmount());
    customer = customerRepository.save(customer);
    PaymentEntity payment = new PaymentEntity();
    payment.setCustomer(customer);
    payment.setDate(LocalDateTime.now());
    payment.setDistrict(district);
    payment.setData(warehouse.getName() + "    " + district.getName());
    payment.setAmount(req.getAmount());
    payment = paymentRepository.save(payment);
    PaymentResponse res = new PaymentResponse(req);
    res.setPaymentId(payment.getId());
    res.setCustomerCredit(customer.getCredit());
    res.setCustomerCreditLimit(customer.getCreditLimit());
    res.setCustomerDiscount(customer.getDiscount());
    res.setCustomerBalance(customer.getBalance());
    return res;
  }
}
