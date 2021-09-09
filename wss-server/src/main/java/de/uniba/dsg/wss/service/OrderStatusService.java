package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;

/**
 * This service implements the order status business transaction.
 *
 * <p>This transaction selects the last order of the customer specified in the given request object
 * and returns some of the order data as part of the response.
 *
 * @author Benedikt Full
 */
public abstract class OrderStatusService
    implements TransactionService<OrderStatusRequest, OrderStatusResponse> {

  public OrderStatusService() {}
}
