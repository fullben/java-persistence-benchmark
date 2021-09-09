package de.uniba.dsg.wss.api.ms;

import de.uniba.dsg.wss.api.TransactionsController;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.wss.service.ms.MsNewOrderService;
import de.uniba.dsg.wss.service.ms.MsOrderStatusService;
import de.uniba.dsg.wss.service.ms.MsPaymentService;
import de.uniba.dsg.wss.service.ms.MsStockLevelService;
import de.uniba.dsg.wss.service.ms.MsDeliveryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides access to the services of the server when launched in MS persistence
 * mode.
 *
 * @author Benedikt Full
 */
@RestController
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsTransactionsController implements TransactionsController {

  private final MsNewOrderService newOrderService;
  private final MsPaymentService paymentService;
  private final MsOrderStatusService orderStatusService;
  private final MsDeliveryService deliveryService;
  private final MsStockLevelService stockLevelService;

  public MsTransactionsController(MsNewOrderService newOrderService,
      MsPaymentService paymentService,
      MsOrderStatusService orderStatusService,
      MsDeliveryService deliveryService,
      MsStockLevelService stockLevelService) {
    this.newOrderService = newOrderService;
    this.paymentService = paymentService;
    this.orderStatusService = orderStatusService;
    this.deliveryService = deliveryService;
    this.stockLevelService = stockLevelService;
  }

  @Override
  public NewOrderResponse doNewOrderTransaction(NewOrderRequest req) {
    return newOrderService.process(req);
  }

  @Override
  public PaymentResponse doPaymentTransaction(PaymentRequest req) {
    return paymentService.process(req);
  }

  @Override
  public OrderStatusResponse doOrderStatusTransaction(OrderStatusRequest req) {
    return orderStatusService.process(req);
  }

  @Override
  public DeliveryResponse doDeliveryTransaction(DeliveryRequest req) {
    return deliveryService.process(req);
  }

  @Override
  public StockLevelResponse doStockLevelTransaction(StockLevelRequest req) {
    return stockLevelService.process(req);
  }
}
