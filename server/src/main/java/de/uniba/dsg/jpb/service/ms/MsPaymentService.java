package de.uniba.dsg.jpb.service.ms;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.PaymentData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.jpb.service.PaymentService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsPaymentService extends PaymentService {

  private final DataManager dataManager;

  @Autowired
  public MsPaymentService(DataManager dataManager) {
    this.dataManager = dataManager;
  }

  @Override
  public PaymentResponse process(PaymentRequest req) {
    // Fetch warehouse, district, and customer (either by id or email)
    WarehouseData warehouse = null;
    DistrictData district = findDistrictById(req.getDistrictId(), warehouse.getDistricts());
    Long customerId = req.getCustomerId();
    CustomerData customer;
    if (customerId == null) {
      // TODO get by email
      customer = null;
    } else {
      // TODO get by id
      customer = null;
    }

    // Update warehouse and district year to data balance
    warehouse.setYearToDateBalance(warehouse.getYearToDateBalance() + req.getAmount());
    // warehouse = warehouseRepository.save(warehouse);
    district.setYearToDateBalance(district.getYearToDateBalance() + req.getAmount());
    // TODO save district

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
    // TODO save customer

    // Create a new entry for this payment
    PaymentData payment = new PaymentData();
    payment.setCustomer(customer);
    customer.getPayments().add(payment);
    payment.setDate(LocalDateTime.now());
    payment.setDistrict(district);
    payment.setData(buildPaymentData(warehouse.getName(), district.getName()));
    payment.setAmount(req.getAmount());
    // TODO save payment AND customer

    PaymentResponse res = new PaymentResponse(req);
    res.setPaymentId(payment.getId());
    res.setCustomerCredit(customer.getCredit());
    res.setCustomerCreditLimit(customer.getCreditLimit());
    res.setCustomerDiscount(customer.getDiscount());
    res.setCustomerBalance(customer.getBalance());
    return res;
  }

  private static DistrictData findDistrictById(Long id, List<DistrictData> districts) {
    return districts.stream()
        .filter(d -> d.getId().equals(id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }
}
