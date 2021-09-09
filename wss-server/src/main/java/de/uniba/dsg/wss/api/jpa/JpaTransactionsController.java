package de.uniba.dsg.wss.api.jpa;

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
import de.uniba.dsg.wss.service.jpa.JpaDeliveryService;
import de.uniba.dsg.wss.service.jpa.JpaNewOrderService;
import de.uniba.dsg.wss.service.jpa.JpaOrderStatusService;
import de.uniba.dsg.wss.service.jpa.JpaPaymentService;
import de.uniba.dsg.wss.service.jpa.JpaStockLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
