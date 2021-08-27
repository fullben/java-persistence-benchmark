package de.uniba.dsg.jpb.api;

import de.uniba.dsg.jpb.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.jpb.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelResponse;

/**
 * Implementations of this controller allow clients to interact with the services of this server.
 * These services are the implementations of the business transactions.
 *
 * @author Benedikt Full
 */
public interface TransactionsController {

  NewOrderResponse doNewOrderTransaction(NewOrderRequest req);

  PaymentResponse doPaymentTransaction(PaymentRequest req);

  OrderStatusResponse doOrderStatusTransaction(OrderStatusRequest req);

  DeliveryResponse doDeliveryTransaction(DeliveryRequest req);

  StockLevelResponse doStockLevelTransaction(StockLevelRequest req);
}
