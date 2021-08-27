package de.uniba.dsg.jpb.service;

import de.uniba.dsg.jpb.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.jpb.data.transfer.messages.DeliveryResponse;

/**
 * This service implements the delivery business transaction.
 *
 * <p>In this transaction, up to 10 not yet fulfilled orders are 'delivered'. For this, a carrier is
 * assigned, the fulfillment state is updated and the delivery dates of the individual items are
 * set. The orders are selected by looking for the oldest, un-fulfilled order of each of the 10
 * districts of the warehouse specified in the given request object.
 *
 * @author Benedikt Full
 */
public abstract class DeliveryService
    implements TransactionService<DeliveryRequest, DeliveryResponse> {

  public DeliveryService() {}
}
