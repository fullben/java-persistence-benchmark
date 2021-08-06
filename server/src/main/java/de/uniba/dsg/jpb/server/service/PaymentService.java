package de.uniba.dsg.jpb.server.service;

import de.uniba.dsg.jpb.messages.PaymentRequest;
import de.uniba.dsg.jpb.messages.PaymentResponse;

public abstract class PaymentService
    implements TransactionService<PaymentRequest, PaymentResponse> {

  public PaymentService() {}
}
