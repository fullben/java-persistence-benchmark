package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.access.ms.DataConsistencyManager;
import de.uniba.dsg.wss.data.model.ms.*;
import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.wss.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implements the mentioned transactions in the README.
 *
 * @author Johannes Manner
 */
@Service
public class MsPaymentService extends PaymentService {

  private final DataConsistencyManager consistencyManager;
  private final MsDataRoot dataRoot;

  @Autowired
  public MsPaymentService(DataConsistencyManager consistencyManager, MsDataRoot dataRoot) {
    this.consistencyManager = consistencyManager;
    this.dataRoot = dataRoot;
  }

  @Override
  public PaymentResponse process(PaymentRequest req) {

    CustomerData customer;
    if(req.getCustomerId() == null) {
      customer = dataRoot.getCustomers().entrySet().stream()
              .parallel()
              .filter(c -> c.getValue().getEmail().equals(req.getCustomerEmail()))
              .findAny()
              .orElseThrow(() -> new IllegalStateException("Failed to find customer with email " + req.getCustomerEmail()))
      .getValue();
    } else {
      customer = dataRoot.getCustomers().get(req.getCustomerId());
      if(customer == null) {
        throw new IllegalStateException("Failed to find customer with email " + req.getCustomerEmail());
      }
    }

    WarehouseData warehouseData = dataRoot.getWarehouses().get(req.getWarehouseId());
    DistrictData districtData = warehouseData.getDistricts().get(req.getDistrictId());

    // create new payment
    PaymentData payment = new PaymentData(customer,
            LocalDateTime.now(),
            req.getAmount(),
            buildPaymentData(warehouseData.getName(), districtData.getName()));

    // update payment and dependent objects
    // copy the customer data is here important since there could be concurrent updates on the same customer object...
    CustomerData copiedCustomer = this.consistencyManager.storePaymentAndUpdateDependentObjects(warehouseData, districtData, customer, payment);

    // building response object...
    PaymentResponse res = new PaymentResponse(req);
    res.setPaymentId(payment.getId());
    res.setCustomerId(copiedCustomer.getId());
    res.setCustomerCredit(copiedCustomer.getCredit());
    res.setCustomerCreditLimit(copiedCustomer.getCreditLimit());
    res.setCustomerDiscount(copiedCustomer.getDiscount());
    res.setCustomerBalance(copiedCustomer.getBalance());
    return res;
  }
}
