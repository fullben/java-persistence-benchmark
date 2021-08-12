package de.uniba.dsg.jpb.server.service;

import de.uniba.dsg.jpb.server.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.server.messages.OrderStatusResponse;

public abstract class OrderStatusService
    implements TransactionService<OrderStatusRequest, OrderStatusResponse> {

  public OrderStatusService() {}
}
