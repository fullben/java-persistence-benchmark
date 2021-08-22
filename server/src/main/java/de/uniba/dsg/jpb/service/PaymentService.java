package de.uniba.dsg.jpb.service;

import de.uniba.dsg.jpb.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentResponse;

public abstract class PaymentService
    implements TransactionService<PaymentRequest, PaymentResponse> {

  public PaymentService() {}

  protected String buildPaymentData(String warehouseName, String districtName) {
    return warehouseName + "    " + districtName;
  }

  protected String buildNewCustomerData(
      long customerId, long warehouseId, long districtId, double amount, String oldData) {
    String newData = "" + customerId + districtId + warehouseId + amount;
    return newData + oldData.substring(newData.length());
  }

  protected boolean customerHasBadCredit(String customerCredit) {
    return "BC".equals(customerCredit);
  }
}
