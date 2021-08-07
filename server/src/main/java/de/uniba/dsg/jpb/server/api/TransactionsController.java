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
import de.uniba.dsg.jpb.server.service.jpa.JpaDeliveryService;
import de.uniba.dsg.jpb.server.service.jpa.JpaNewOrderService;
import de.uniba.dsg.jpb.server.service.jpa.JpaOrderStatusService;
import de.uniba.dsg.jpb.server.service.jpa.JpaPaymentService;
import de.uniba.dsg.jpb.server.service.jpa.JpaStockLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TransactionsController {

  private final JpaNewOrderService jpaNewOrderService;
  private final JpaPaymentService jpaPaymentService;
  private final JpaOrderStatusService jpaOrderStatusService;
  private final JpaDeliveryService jpaDeliveryService;
  private final JpaStockLevelService jpaStockLevelService;

  @Autowired
  public TransactionsController(
      JpaNewOrderService jpaNewOrderService,
      JpaPaymentService jpaPaymentService,
      JpaOrderStatusService jpaOrderStatusService,
      JpaDeliveryService jpaDeliveryService,
      JpaStockLevelService jpaStockLevelService) {
    this.jpaNewOrderService = jpaNewOrderService;
    this.jpaPaymentService = jpaPaymentService;
    this.jpaOrderStatusService = jpaOrderStatusService;
    this.jpaDeliveryService = jpaDeliveryService;
    this.jpaStockLevelService = jpaStockLevelService;
  }

  @PostMapping(value = "transactions/new-order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public OrderResponse doNewOrderTransaction(@RequestBody OrderRequest req) {
    return jpaNewOrderService.process(req);
  }

  @PostMapping(value = "transactions/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public PaymentResponse doPaymentTransaction(@RequestBody PaymentRequest req) {
    return jpaPaymentService.process(req);
  }

  @PostMapping(
      value = "transactions/order-status",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public OrderStatusResponse doOrderStatusTransaction(@RequestBody OrderStatusRequest req) {
    return jpaOrderStatusService.process(req);
  }

  @PostMapping(value = "transactions/delivery", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public DeliveryResponse doDeliveryTransaction(@RequestBody DeliveryRequest req) {
    return jpaDeliveryService.process(req);
  }

  @PostMapping(value = "transactions/stock-level", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public StockLevelResponse doStockLevelTransaction(@RequestBody StockLevelRequest req) {
    return jpaStockLevelService.process(req);
  }
}
