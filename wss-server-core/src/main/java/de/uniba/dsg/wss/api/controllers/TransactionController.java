package de.uniba.dsg.wss.api.controllers;

import de.uniba.dsg.wss.api.ApiResponse;
import de.uniba.dsg.wss.api.BadRequestException;
import de.uniba.dsg.wss.auth.Privileges;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryRequest;
import de.uniba.dsg.wss.data.transfer.messages.DeliveryResponse;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusRequest;
import de.uniba.dsg.wss.data.transfer.messages.OrderStatusResponse;
import de.uniba.dsg.wss.data.transfer.messages.PaymentRequest;
import de.uniba.dsg.wss.data.transfer.messages.PaymentResponse;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import de.uniba.dsg.wss.service.DeliveryService;
import de.uniba.dsg.wss.service.NewOrderService;
import de.uniba.dsg.wss.service.OrderStatusService;
import de.uniba.dsg.wss.service.PaymentService;
import de.uniba.dsg.wss.service.StockLevelService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller allows clients to interact with the services of this server. These services are
 * the implementations of the business transactions.
 *
 * <p>Note that all responses from endpoints defined in this controller will include the {@link
 * ApiResponse#REQUEST_PROCESSING_NANOS_HEADER_NAME REQUEST_PROCESSING_NANOS_HEADER_NAME} header.
 * The value of this header is the time it took the service handling the endpoint to process the
 * request. This processing duration is the 'raw' service processing duration. This means that
 * request parsing and (de)serialization are not included.
 *
 * @author Benedikt Full
 */
@RestController
@RequestMapping("api")
@Validated
@PreAuthorize("hasAuthority('" + Privileges.EXECUTE_BUSINESS_TRANSACTIONS_ALL + "')")
public class TransactionController {

  private final NewOrderService newOrderService;
  private final PaymentService paymentService;
  private final OrderStatusService orderStatusService;
  private final DeliveryService deliveryService;
  private final StockLevelService stockLevelService;

  @Autowired
  public TransactionController(
      NewOrderService newOrderService,
      PaymentService paymentService,
      OrderStatusService orderStatusService,
      DeliveryService deliveryService,
      StockLevelService stockLevelService) {
    this.newOrderService = newOrderService;
    this.paymentService = paymentService;
    this.orderStatusService = orderStatusService;
    this.deliveryService = deliveryService;
    this.stockLevelService = stockLevelService;
  }

  @PostMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/customers/{customerId}/orders",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(
      summary = "New-order transaction",
      description =
          "Creates a new customer order for the specified customer and with the provided items.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Data describing the newly created order")
      })
  public ResponseEntity<NewOrderResponse> doNewOrderTransaction(
      @PathVariable(name = "warehouseId") @NotBlank String warehouseId,
      @PathVariable(name = "districtId") @NotBlank String districtId,
      @PathVariable(name = "customerId") @NotBlank String customerId,
      @RequestBody @NotEmpty List<NewOrderRequestItem> items) {
    NewOrderRequest req = new NewOrderRequest(warehouseId, districtId, customerId, items);
    return ApiResponse.created().withDurationHeader().withBody(() -> newOrderService.process(req));
  }

  @PostMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/customers/payments",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(
      summary = "Payment transaction",
      description =
          "Creates a customer payment for the identified customer and with the amount specified in the body.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Data describing the newly created payment"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Neither customer ID nor email were provided")
      })
  public ResponseEntity<PaymentResponse> doPaymentTransaction(
      @PathVariable(name = "warehouseId") @NotBlank String warehouseId,
      @PathVariable(name = "districtId") @NotBlank String districtId,
      @RequestParam(name = "customerId", required = false) String customerId,
      @RequestParam(name = "customerEmail", required = false) String customerEmail,
      @RequestBody @Min(value = 1) int amount) {
    requireEitherCustomerIdOrEmail(customerId, customerEmail);
    PaymentRequest req =
        new PaymentRequest(warehouseId, districtId, customerId, customerEmail, amount);
    return ApiResponse.created().withDurationHeader().withBody(() -> paymentService.process(req));
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/districts/{districtId}/customers/orders/status",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(
      summary = "Order-status transaction",
      description =
          "Returns status data regarding the most recent order of the customer specified.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Data regarding the specified customer and their most recent order"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Neither customer ID nor email were provided")
      })
  public ResponseEntity<OrderStatusResponse> doOrderStatusTransaction(
      @PathVariable(name = "warehouseId") @NotBlank String warehouseId,
      @PathVariable(name = "districtId") @NotBlank String districtId,
      @RequestParam(name = "customerId", required = false) String customerId,
      @RequestParam(name = "customerEmail", required = false) String customerEmail) {
    requireEitherCustomerIdOrEmail(customerId, customerEmail);
    OrderStatusRequest req =
        new OrderStatusRequest(warehouseId, districtId, customerId, customerEmail);
    return ApiResponse.ok().withDurationHeader().withBody(() -> orderStatusService.process(req));
  }

  @PostMapping(
      value = "warehouses/{warehouseId}/deliveries",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(
      summary = "Delivery transaction",
      description =
          "Updates the delivery status of up to 10 undelivered orders from the specified warehouse.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "The given warehouse and carrier ID")
      })
  public ResponseEntity<DeliveryResponse> doDeliveryTransaction(
      @PathVariable(name = "warehouseId") @NotBlank String warehouseId,
      @RequestBody @NotBlank String carrierId) {
    DeliveryRequest req = new DeliveryRequest(warehouseId, carrierId);
    return ApiResponse.created().withDurationHeader().withBody(() -> deliveryService.process(req));
  }

  @GetMapping(
      value = "warehouses/{warehouseId}/stock-levels",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Operation(
      summary = "Stock-level transaction",
      description =
          "Checks the stock levels of items affected by the 20 most recent orders for the specified district for whether they are below a certain threshold and returns the number of affected stocks",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description =
                "Some of the provided data and the number of stocks that are below the specified threshold")
      })
  public ResponseEntity<StockLevelResponse> doStockLevelTransaction(
      @PathVariable(name = "warehouseId") @NotBlank String warehouseId,
      @RequestParam(name = "districtId") @NotBlank String districtId,
      @RequestParam(name = "threshold") @Min(value = 10) int threshold) {
    StockLevelRequest req = new StockLevelRequest(warehouseId, districtId, threshold);
    return ApiResponse.ok().withDurationHeader().withBody(() -> stockLevelService.process(req));
  }

  private void requireEitherCustomerIdOrEmail(String id, String email) throws BadRequestException {
    if (id == null && email == null) {
      throw new BadRequestException("Either customer id or email must be provided");
    }
  }
}
