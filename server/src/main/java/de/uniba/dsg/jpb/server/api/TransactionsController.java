package de.uniba.dsg.jpb.server.api;

import de.uniba.dsg.jpb.server.messages.DeliveryRequest;
import de.uniba.dsg.jpb.server.messages.DeliveryResponse;
import de.uniba.dsg.jpb.server.messages.OrderRequest;
import de.uniba.dsg.jpb.server.messages.OrderResponse;
import de.uniba.dsg.jpb.server.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.server.messages.OrderStatusResponse;
import de.uniba.dsg.jpb.server.messages.PaymentRequest;
import de.uniba.dsg.jpb.server.messages.PaymentResponse;
import de.uniba.dsg.jpb.server.messages.StockLevelRequest;
import de.uniba.dsg.jpb.server.messages.StockLevelResponse;

public interface TransactionsController {

  OrderResponse doNewOrderTransaction(OrderRequest req);

  PaymentResponse doPaymentTransaction(PaymentRequest req);

  OrderStatusResponse doOrderStatusTransaction(OrderStatusRequest req);

  DeliveryResponse doDeliveryTransaction(DeliveryRequest req);

  StockLevelResponse doStockLevelTransaction(StockLevelRequest req);
}
