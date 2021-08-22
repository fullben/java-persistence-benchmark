package de.uniba.dsg.jpb.transaction;

import de.uniba.dsg.jpb.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentResponse;

public abstract class PaymentService
    implements TransactionService<PaymentRequest, PaymentResponse> {

  public PaymentService() {}
}
