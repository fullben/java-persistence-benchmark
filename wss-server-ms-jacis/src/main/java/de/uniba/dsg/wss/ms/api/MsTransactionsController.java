package de.uniba.dsg.wss.ms.api;

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
import de.uniba.dsg.wss.ms.service.MsDeliveryService;
import de.uniba.dsg.wss.ms.service.MsNewOrderService;
import de.uniba.dsg.wss.ms.service.MsOrderStatusService;
import de.uniba.dsg.wss.ms.service.MsPaymentService;
import de.uniba.dsg.wss.ms.service.MsStockLevelService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
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

  public MsTransactionsController(
      MsNewOrderService newOrderService,
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
  public ResponseEntity<NewOrderResponse> doNewOrderTransaction(NewOrderRequest req) {
    return ResponseEntity.ok(newOrderService.process(req));
  }

  @Override
  public ResponseEntity<PaymentResponse> doPaymentTransaction(PaymentRequest req) {
    return ResponseEntity.ok(paymentService.process(req));
  }

  @Override
  public ResponseEntity<OrderStatusResponse> doOrderStatusTransaction(OrderStatusRequest req) {
    return ResponseEntity.ok(orderStatusService.process(req));
  }

  @Override
  public ResponseEntity<DeliveryResponse> doDeliveryTransaction(DeliveryRequest req) {
    return ResponseEntity.ok(deliveryService.process(req));
  }

  @Override
  public ResponseEntity<StockLevelResponse> doStockLevelTransaction(StockLevelRequest req) {
    return ResponseEntity.ok(stockLevelService.process(req));
  }
}
