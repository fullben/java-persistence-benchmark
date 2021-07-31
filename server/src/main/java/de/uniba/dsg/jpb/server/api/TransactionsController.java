package de.uniba.dsg.jpb.server.api;

import de.uniba.dsg.jpb.server.services.NewOrderService;
import de.uniba.dsg.jpb.messages.OrderRequest;
import de.uniba.dsg.jpb.messages.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TransactionsController {

  private final NewOrderService newOrderService;

  @Autowired
  public TransactionsController(NewOrderService newOrderService) {
    this.newOrderService = newOrderService;
  }

  @PostMapping(value = "transactions/new-order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public OrderResponse newOrderTransaction(@RequestBody OrderRequest req) {
    return newOrderService.process(req);
  }
}
