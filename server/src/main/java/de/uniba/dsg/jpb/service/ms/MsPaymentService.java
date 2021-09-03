package de.uniba.dsg.jpb.service.ms;

import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.PaymentData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.jpb.service.PaymentService;
import java.time.LocalDateTime;
import org.jacis.container.JacisContainer;
import org.jacis.store.JacisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsPaymentService extends PaymentService {

  private final JacisContainer container;
  private final JacisStore<String, WarehouseData> warehouseStore;
  private final JacisStore<String, DistrictData> districtStore;
  private final JacisStore<String, CustomerData> customerStore;
  private final JacisStore<String, PaymentData> paymentStore;

  @Autowired
  public MsPaymentService(
      JacisContainer container,
      JacisStore<String, WarehouseData> warehouseStore,
      JacisStore<String, DistrictData> districtStore,
      JacisStore<String, CustomerData> customerStore,
      JacisStore<String, PaymentData> paymentStore) {
    this.container = container;
    this.warehouseStore = warehouseStore;
    this.districtStore = districtStore;
    this.customerStore = customerStore;
    this.paymentStore = paymentStore;
  }

  @Override
  public PaymentResponse process(PaymentRequest req) {
    return container.withLocalTx(
        () -> {
          // Find customer (either by id or email)
          String customerId = req.getCustomerId();
          CustomerData customer;
          if (customerId == null) {
            customer =
                customerStore.stream(c -> c.getEmail().equals(req.getCustomerEmail()))
                    .findAny()
                    .orElseThrow(
                        () ->
                            new IllegalStateException(
                                "Failed to find customer with email " + req.getCustomerEmail()));
          } else {
            customer =
                customerStore.stream(c -> c.getId().equals(customerId))
                    .findAny()
                    .orElseThrow(
                        () ->
                            new IllegalStateException(
                                "Failed to find customer with id " + customerId));
          }

          // Update warehouse and district year to data balance
          WarehouseData warehouse = warehouseStore.get(req.getWarehouseId());
          warehouse.setYearToDateBalance(warehouse.getYearToDateBalance() + req.getAmount());
          warehouseStore.update(warehouse.getId(), warehouse);
          DistrictData district = districtStore.get(req.getDistrictId());
          district.setYearToDateBalance(district.getYearToDateBalance() + req.getAmount());
          districtStore.update(district.getId(), district);

          // Update customer balance, year to data payment, and payment count
          final double customerBalance = customer.getBalance();
          final double customerYearToDatePayment = customer.getYearToDatePayment();
          customer.setBalance(customerBalance - req.getAmount());
          customer.setYearToDatePayment(customerYearToDatePayment + req.getAmount());
          customer.setPaymentCount(customer.getPaymentCount() + 1);
          customerStore.update(customer.getId(), customer);
          // Update customer data if the customer has bad credit
          final String customerData = customer.getData();
          if (customerHasBadCredit(customer.getCredit())) {
            customer.setData(
                buildNewCustomerData(
                    customer.getId(),
                    customer.getDistrictId(),
                    customer.getWarehouseId(),
                    req.getAmount(),
                    customerData));
          }

          // Create a new entry for this payment
          PaymentData payment = new PaymentData();
          payment.setCustomerId(customer.getId());
          payment.setDistrictId(district.getId());
          payment.setDate(LocalDateTime.now());
          payment.setAmount(req.getAmount());
          payment.setData(buildPaymentData(warehouse.getName(), district.getName()));
          paymentStore.update(payment.getId(), payment);

          PaymentResponse res = new PaymentResponse(req);
          res.setPaymentId(payment.getId());
          res.setCustomerId(customer.getId());
          res.setCustomerCredit(customer.getCredit());
          res.setCustomerCreditLimit(customer.getCreditLimit());
          res.setCustomerDiscount(customer.getDiscount());
          res.setCustomerBalance(customer.getBalance());
          return res;
        });
  }
}
