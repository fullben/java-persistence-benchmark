package de.uniba.dsg.jpb.api.jpa;

import de.uniba.dsg.jpb.api.TransactionsController;
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
import de.uniba.dsg.jpb.transaction.jpa.JpaDeliveryService;
import de.uniba.dsg.jpb.transaction.jpa.JpaNewOrderService;
import de.uniba.dsg.jpb.transaction.jpa.JpaOrderStatusService;
import de.uniba.dsg.jpb.transaction.jpa.JpaPaymentService;
import de.uniba.dsg.jpb.transaction.jpa.JpaStockLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
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

  @PostMapping(value = "transactions/new-order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public OrderResponse doNewOrderTransaction(@RequestBody OrderRequest req) {
    return newOrderService.process(req);
  }

  @PostMapping(value = "transactions/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public PaymentResponse doPaymentTransaction(@RequestBody PaymentRequest req) {
    return paymentService.process(req);
  }

  @GetMapping(value = "transactions/order-status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public OrderStatusResponse doOrderStatusTransaction(@RequestBody OrderStatusRequest req) {
    return orderStatusService.process(req);
  }

  @PutMapping(value = "transactions/delivery", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public DeliveryResponse doDeliveryTransaction(@RequestBody DeliveryRequest req) {
    return deliveryService.process(req);
  }

  @GetMapping(value = "transactions/stock-level", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public StockLevelResponse doStockLevelTransaction(@RequestBody StockLevelRequest req) {
    return stockLevelService.process(req);
  }
}
