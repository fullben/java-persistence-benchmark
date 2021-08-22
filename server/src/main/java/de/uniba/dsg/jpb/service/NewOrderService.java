package de.uniba.dsg.jpb.service;

import de.uniba.dsg.jpb.data.transfer.messages.OrderRequest;
import de.uniba.dsg.jpb.data.transfer.messages.OrderResponse;
import de.uniba.dsg.jpb.util.UniformRandom;
import java.time.LocalDateTime;
import java.util.List;

public abstract class NewOrderService implements TransactionService<OrderRequest, OrderResponse> {

  private static final UniformRandom DIST_RANDOM = new UniformRandom(1, 10);

  public NewOrderService() {}

  protected String randomDistrictData(List<String> districtData) {
    if (districtData.size() != 10) {
      throw new IllegalArgumentException();
    }
    return districtData.get(randomDistrictNumber());
  }

  private static int randomDistrictNumber() {
    return DIST_RANDOM.nextInt();
  }

  protected String determineBrandGeneric(String itemData, String stockData) {
    final String s = "ORIGINAL";
    return itemData.contains(s) || stockData.contains(s) ? "B" : "G";
  }

  protected static OrderResponse newOrderResponse(
      OrderRequest req,
      Long orderId,
      LocalDateTime orderEntryDate,
      double warehouseSalesTax,
      double districtSalesTax,
      String customerCredit,
      double customerDiscount,
      String customerLastName) {
    OrderResponse res = new OrderResponse(req);
    res.setOrderId(orderId);
    res.setOrderTimestamp(orderEntryDate);
    res.setWarehouseSalesTax(warehouseSalesTax);
    res.setDistrictSalesTax(districtSalesTax);
    res.setOrderItemCount(req.getItems().size());
    res.setCustomerCredit(customerCredit);
    res.setCustomerDiscount(customerDiscount);
    res.setCustomerLastName(customerLastName);
    return res;
  }

  protected static double calcOrderTotal(
      double sumPrice, double customerDiscount, double warehouseSalesTax, double districtSalesTax) {
    if (sumPrice < 0
        || customerDiscount < 0
        || customerDiscount > 1
        || warehouseSalesTax < 0
        || districtSalesTax < 0) {
      throw new IllegalArgumentException();
    }
    return sumPrice * (1 - customerDiscount) * (1 + warehouseSalesTax + districtSalesTax);
  }

  protected int determineNewStockQuantity(int stockQuantity, int orderItemQuantity) {
    if (stockQuantity + 10 > orderItemQuantity) {
      return stockQuantity - orderItemQuantity;
    } else {
      return stockQuantity - orderItemQuantity + 91;
    }
  }
}
