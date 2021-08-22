package de.uniba.dsg.jpb.api;

import de.uniba.dsg.jpb.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.jpb.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.jpb.data.transfer.messages.OrderRequest;
import de.uniba.dsg.jpb.data.transfer.messages.OrderResponse;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelResponse;

public interface TransactionsController {

  OrderResponse doNewOrderTransaction(OrderRequest req);

  PaymentResponse doPaymentTransaction(PaymentRequest req);

  OrderStatusResponse doOrderStatusTransaction(OrderStatusRequest req);

  DeliveryResponse doDeliveryTransaction(DeliveryRequest req);

  StockLevelResponse doStockLevelTransaction(StockLevelRequest req);
}
