package de.uniba.dsg.jpb.service.ms;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.Find;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.PaymentData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.jpb.service.PaymentService;
import java.time.LocalDateTime;
import one.microstream.persistence.types.Storer;
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
    return dataManager.write(
        (root, storageManager) -> {
          // Find warehouse, district, and customer (either by id or email)
          WarehouseData warehouse =
              Find.warehouseById(req.getWarehouseId(), root.findAllWarehouses());
          DistrictData district = Find.districtById(req.getDistrictId(), warehouse);
          String customerId = req.getCustomerId();
          CustomerData customer;
          if (customerId == null) {
            customer = Find.customerByEmail(req.getCustomerEmail(), district);
          } else {
            customer = Find.customerById(customerId, district);
          }

          // Update warehouse and district year to data balance
          final double warehouseYearToDateBalance = warehouse.getYearToDateBalance();
          warehouse.setYearToDateBalance(warehouseYearToDateBalance + req.getAmount());
          final double districtYearToDateBalance = district.getYearToDateBalance();
          district.setYearToDateBalance(districtYearToDateBalance + req.getAmount());

          // Update customer balance, year to data payment, and payment count
          final double customerBalance = customer.getBalance();
          final double customerYearToDatePayment = customer.getYearToDatePayment();
          customer.setBalance(customerBalance - req.getAmount());
          customer.setYearToDatePayment(customerYearToDatePayment + req.getAmount());
          customer.setPaymentCount(customer.getPaymentCount() + 1);
          // Update customer data if the customer has bad credit
          final String customerData = customer.getData();
          if (customerHasBadCredit(customer.getCredit())) {
            customer.setData(
                buildNewCustomerData(
                    customer.getId(),
                    customer.getDistrict().getId(),
                    customer.getDistrict().getWarehouse().getId(),
                    req.getAmount(),
                    customerData));
          }

          // Create a new entry for this payment
          PaymentData payment = new PaymentData();
          payment.setCustomer(customer);
          customer.getPayments().add(payment);
          payment.setDate(LocalDateTime.now());
          payment.setDistrict(district);
          payment.setData(buildPaymentData(warehouse.getName(), district.getName()));
          payment.setAmount(req.getAmount());

          try {
            // Persist the changes
            Storer storer = storageManager.createEagerStorer();
            storer.storeAll(payment, customer, warehouse, district);
            storer.commit();
          } catch (RuntimeException e) {
            // Reset warehouse and district balances
            warehouse.setYearToDateBalance(warehouseYearToDateBalance);
            district.setYearToDateBalance(districtYearToDateBalance);
            // Reset customer data
            customer.setBalance(customerBalance);
            customer.setYearToDatePayment(customerYearToDatePayment);
            customer.setPaymentCount(customer.getPaymentCount() - 1);
            customer.setData(customerData);
            // Detach new payments object from graph
            customer.getPayments().remove(payment);
            throw e;
          }

          PaymentResponse res = new PaymentResponse(req);
          res.setPaymentId(payment.getId());
          res.setCustomerCredit(customer.getCredit());
          res.setCustomerCreditLimit(customer.getCreditLimit());
          res.setCustomerDiscount(customer.getDiscount());
          res.setCustomerBalance(customer.getBalance());
          return res;
        });
  }
}
