package de.uniba.dsg.wss.api;

import de.uniba.dsg.wss.data.transfer.messages.*;
import de.uniba.dsg.wss.service.ms.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides access to the services of the server when launched in MS persistence
 * mode.
 *
 * @author Benedikt Full
 */
@RestController
public class MsTransactionController implements TransactionController {

  private final MsNewOrderService newOrderService;
  private final MsPaymentService paymentService;
  private final MsOrderStatusService orderStatusService;
  private final MsDeliveryService deliveryService;
  private final MsStockLevelService stockLevelService;

  public MsTransactionController(
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
    return ApiResponse.ok().withDurationHeader().withBody(() -> newOrderService.process(req));
  }

  @Override
  public ResponseEntity<PaymentResponse> doPaymentTransaction(PaymentRequest req) {
    return ApiResponse.ok().withDurationHeader().withBody(() -> paymentService.process(req));
  }

  @Override
  public ResponseEntity<OrderStatusResponse> doOrderStatusTransaction(OrderStatusRequest req) {
    return ApiResponse.ok().withDurationHeader().withBody(() -> orderStatusService.process(req));
  }

  @Override
  public ResponseEntity<DeliveryResponse> doDeliveryTransaction(DeliveryRequest req) {
    return ApiResponse.ok().withDurationHeader().withBody(() -> deliveryService.process(req));
  }

  @Override
  public ResponseEntity<StockLevelResponse> doStockLevelTransaction(StockLevelRequest req) {
    return ApiResponse.ok().withDurationHeader().withBody(() -> stockLevelService.process(req));
  }
}