package de.uniba.dsg.jpb.api.ms;

import de.uniba.dsg.jpb.api.TransactionsController;
import de.uniba.dsg.jpb.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.jpb.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.jpb.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.jpb.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.jpb.service.ms.MsDeliveryService;
import de.uniba.dsg.jpb.service.ms.MsNewOrderService;
import de.uniba.dsg.jpb.service.ms.MsOrderStatusService;
import de.uniba.dsg.jpb.service.ms.MsPaymentService;
import de.uniba.dsg.jpb.service.ms.MsStockLevelService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides access to the services of the server when launched in MS persistence
 * mode.
 *
 * @author Benedikt Full
 */
@RestController
@RequestMapping("api")
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
@Validated
public class MsTransactionsController implements TransactionsController {

  private final MsNewOrderService newOrderService;
  private final MsPaymentService paymentService;
  private final MsOrderStatusService orderStatusService;
  private final MsDeliveryService deliveryService;
  private final MsStockLevelService stockLevelService;

  @Autowired
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

  @PostMapping(value = "transactions/new-order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public NewOrderResponse doNewOrderTransaction(@Valid @RequestBody NewOrderRequest req) {
    return newOrderService.process(req);
  }

  @PostMapping(value = "transactions/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public PaymentResponse doPaymentTransaction(@Valid @RequestBody PaymentRequest req) {
    return paymentService.process(req);
  }

  @Override
  public OrderStatusResponse doOrderStatusTransaction(@Valid @RequestBody OrderStatusRequest req) {
    return orderStatusService.process(req);
  }

  @Override
  public DeliveryResponse doDeliveryTransaction(@Valid @RequestBody DeliveryRequest req) {
    return deliveryService.process(req);
  }

  @Override
  public StockLevelResponse doStockLevelTransaction(@Valid @RequestBody StockLevelRequest req) {
    return stockLevelService.process(req);
  }
}
