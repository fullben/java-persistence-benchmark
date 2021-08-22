package de.uniba.dsg.jpb.service;

import de.uniba.dsg.jpb.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.jpb.data.transfer.messages.DeliveryResponse;

public abstract class DeliveryService
    implements TransactionService<DeliveryRequest, DeliveryResponse> {

  public DeliveryService() {}
}
