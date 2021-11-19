package de.uniba.dsg.wss.jpa.api;

import de.uniba.dsg.wss.api.ApiResponse;
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
import de.uniba.dsg.wss.jpa.service.JpaDeliveryService;
import de.uniba.dsg.wss.jpa.service.JpaNewOrderService;
import de.uniba.dsg.wss.jpa.service.JpaOrderStatusService;
import de.uniba.dsg.wss.jpa.service.JpaPaymentService;
import de.uniba.dsg.wss.jpa.service.JpaStockLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides access to the services of the server when launched in JPA persistence
 * mode.
 *
 * @author Benedikt Full
 */
@RestController
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaTransactionsController implements TransactionsController {

  private final JpaNewOrderService newOrderService;
  private final JpaPaymentService paymentService;
  private final JpaOrderStatusService orderStatusService;
  private final JpaDeliveryService deliveryService;
  private final JpaStockLevelService stockLevelService;

  @Autowired
  public JpaTransactionsController(
      JpaNewOrderService newOrderService,
      JpaPaymentService paymentService,
      JpaOrderStatusService orderStatusService,
      JpaDeliveryService deliveryService,
      JpaStockLevelService stockLevelService) {
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
