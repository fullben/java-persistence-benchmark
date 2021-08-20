package de.uniba.dsg.jpb.service;

import de.uniba.dsg.jpb.messages.DeliveryRequest;
import de.uniba.dsg.jpb.messages.DeliveryResponse;

public abstract class DeliveryService
    implements TransactionService<DeliveryRequest, DeliveryResponse> {

  public DeliveryService() {}
}
