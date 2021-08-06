package de.uniba.dsg.jpb.server.api;

import de.uniba.dsg.jpb.messages.OrderRequest;
import de.uniba.dsg.jpb.messages.OrderResponse;
import de.uniba.dsg.jpb.messages.PaymentRequest;
import de.uniba.dsg.jpb.messages.PaymentResponse;
import de.uniba.dsg.jpb.server.service.jpa.JpaNewOrderService;
import de.uniba.dsg.jpb.server.service.jpa.JpaPaymentService;
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

  @Autowired
  public TransactionsController(
      JpaNewOrderService jpaNewOrderService, JpaPaymentService jpaPaymentService) {
    this.jpaNewOrderService = jpaNewOrderService;
    this.jpaPaymentService = jpaPaymentService;
  }

  @PostMapping(value = "transactions/new-order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public OrderResponse newOrderTransaction(@RequestBody OrderRequest req) {
    return jpaNewOrderService.process(req);
  }

  @PostMapping(value = "transactions/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public PaymentResponse newOrderTransaction(@RequestBody PaymentRequest req) {
    return jpaPaymentService.process(req);
  }
}
