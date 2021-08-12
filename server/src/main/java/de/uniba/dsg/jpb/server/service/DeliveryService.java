package de.uniba.dsg.jpb.server.service;

import de.uniba.dsg.jpb.server.messages.DeliveryRequest;
import de.uniba.dsg.jpb.server.messages.DeliveryResponse;

public abstract class DeliveryService
    implements TransactionService<DeliveryRequest, DeliveryResponse> {

  public DeliveryService() {}
}
