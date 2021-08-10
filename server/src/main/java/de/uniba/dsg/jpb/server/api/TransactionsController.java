package de.uniba.dsg.jpb.server.api;

import de.uniba.dsg.jpb.messages.DeliveryRequest;
import de.uniba.dsg.jpb.messages.DeliveryResponse;
import de.uniba.dsg.jpb.messages.OrderRequest;
import de.uniba.dsg.jpb.messages.OrderResponse;
import de.uniba.dsg.jpb.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.messages.OrderStatusResponse;
import de.uniba.dsg.jpb.messages.PaymentRequest;
import de.uniba.dsg.jpb.messages.PaymentResponse;
import de.uniba.dsg.jpb.messages.StockLevelRequest;
import de.uniba.dsg.jpb.messages.StockLevelResponse;

public interface TransactionsController {

  OrderResponse doNewOrderTransaction(OrderRequest req);

  PaymentResponse doPaymentTransaction(PaymentRequest req);

  OrderStatusResponse doOrderStatusTransaction(OrderStatusRequest req);

  DeliveryResponse doDeliveryTransaction(DeliveryRequest req);

  StockLevelResponse doStockLevelTransaction(StockLevelRequest req);
}
