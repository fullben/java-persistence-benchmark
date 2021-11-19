package de.uniba.dsg.wss.api;

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
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Implementations of this controller allow clients to interact with the services of this server.
 * These services are the implementations of the business transactions.
 *
 * <p>By contract, all responses provided by the implementations of this interface must include the
 * {@link ApiResponse#REQUEST_PROCESSING_NANOS_HEADER_NAME REQUEST_PROCESSING_NANOS_HEADER_NAME}
 * header, which indicates how many nanoseconds it took to actually process the request. This can be
 * achieved by utilizing the builder provided by methods such as {@link ApiResponse#ok()}.
 *
 * @author Benedikt Full
 */
@RequestMapping("api")
@Validated
public interface TransactionController {

  @PostMapping(value = "transactions/new-order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<NewOrderResponse> doNewOrderTransaction(@Valid @RequestBody NewOrderRequest req);

  @PostMapping(value = "transactions/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<PaymentResponse> doPaymentTransaction(@Valid @RequestBody PaymentRequest req);

  @GetMapping(value = "transactions/order-status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<OrderStatusResponse> doOrderStatusTransaction(
      @Valid @RequestBody OrderStatusRequest req);

  @PutMapping(value = "transactions/delivery", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<DeliveryResponse> doDeliveryTransaction(@Valid @RequestBody DeliveryRequest req);

  @GetMapping(value = "transactions/stock-level", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<StockLevelResponse> doStockLevelTransaction(
      @Valid @RequestBody StockLevelRequest req);
}
