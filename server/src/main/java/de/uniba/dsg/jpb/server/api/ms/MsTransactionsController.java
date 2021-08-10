package de.uniba.dsg.jpb.server.api.ms;

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
import de.uniba.dsg.jpb.server.api.TransactionsController;
import de.uniba.dsg.jpb.server.service.ms.MsNewOrderService;
import de.uniba.dsg.jpb.server.service.ms.MsPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsTransactionsController implements TransactionsController {

  private final MsNewOrderService newOrderService;
  private final MsPaymentService paymentService;

  @Autowired
  public MsTransactionsController(
      MsNewOrderService newOrderService, MsPaymentService paymentService) {
    this.newOrderService = newOrderService;
    this.paymentService = paymentService;
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

  @Override
  public OrderStatusResponse doOrderStatusTransaction(OrderStatusRequest req) {
    return null;
  }

  @Override
  public DeliveryResponse doDeliveryTransaction(DeliveryRequest req) {
    return null;
  }

  @Override
  public StockLevelResponse doStockLevelTransaction(StockLevelRequest req) {
    return null;
  }
}
