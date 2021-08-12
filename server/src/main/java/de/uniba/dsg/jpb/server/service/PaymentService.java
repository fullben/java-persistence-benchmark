package de.uniba.dsg.jpb.server.service;

import de.uniba.dsg.jpb.server.messages.PaymentRequest;
import de.uniba.dsg.jpb.server.messages.PaymentResponse;

public abstract class PaymentService
    implements TransactionService<PaymentRequest, PaymentResponse> {

  public PaymentService() {}
}
