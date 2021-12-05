package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;

/**
 * This service implements the payment business transaction.
 *
 * <p>In this transaction, the customer pays a certain amount (as specified in the given request),
 * which results in updating the corresponding values on the persisted representations of the
 * customer, their district, and warehouse.
 *
 * @author Benedikt Full
 */
public abstract class PaymentService
    implements TransactionService<PaymentRequest, PaymentResponse> {

  public PaymentService() {}

  protected String buildPaymentData(String warehouseName, String districtName) {
    return warehouseName + "    " + districtName;
  }

  protected String buildNewCustomerData(
      String customerId, String warehouseId, String districtId, double amount, String oldData) {
    String newData = customerId + districtId + warehouseId + amount;
    return newData + oldData.substring(newData.length());
  }

  protected boolean customerHasBadCredit(String customerCredit) {
    return "BC".equals(customerCredit);
  }
}
