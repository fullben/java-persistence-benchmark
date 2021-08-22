package de.uniba.dsg.jpb.service;

import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusResponse;

public abstract class OrderStatusService
    implements TransactionService<OrderStatusRequest, OrderStatusResponse> {

  public OrderStatusService() {}
}