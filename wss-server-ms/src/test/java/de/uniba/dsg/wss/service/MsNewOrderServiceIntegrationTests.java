package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.model.ms.StockData;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.wss.service.ms.MsNewOrderService;
import de.uniba.dsg.wss.service.ms.MsTransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MsNewOrderServiceIntegrationTests extends MicroStreamServiceTest {

  @Autowired
  private MsNewOrderService msNewOrderService;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
    adjustDefaults(5, 10);
  }

  private void adjustDefaults(int retries, int increaseQuantity) {
    MsNewOrderService.MAX_RETRIES = retries;
    StockData.INCREASE_QUANTITY = increaseQuantity;
  }

  class ProductToOrder{
    protected String stockId;
    protected String warehouseId;
    protected String productId;
    protected int quantity;

    public ProductToOrder(String warehouseId, String productId, int quantity) {
      // remember the optimization :)
      this.stockId = warehouseId+productId;
      this.warehouseId = warehouseId;
      this.productId = productId;
      this.quantity = quantity;
    }
  }

  @Test
  public void invalidWarehouse() {
    assertThrows(IllegalArgumentException.class, () -> {
      // key -> stock id, value -> quantity
      List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1", "P1", 2));

      NewOrderRequest request = getNewOrderRequest("WW0","D0","C0", productToOrderList);
      this.msNewOrderService.process(request);
    });
  }

  @Test
  public void invalidDistrict() {
    assertThrows(IllegalArgumentException.class, () -> {
      // key -> stock id, value -> quantity
      List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1", "P1", 2));

      NewOrderRequest request = getNewOrderRequest("W0","DD0","C0", productToOrderList);
      this.msNewOrderService.process(request);
    });
  }

  @Test
  public void invalidCustomer() {
    assertThrows(IllegalArgumentException.class, () -> {
      // key -> stock id, value -> quantity
      List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1", "P1", 2));

      NewOrderRequest request = getNewOrderRequest("W0","D0","CC0", productToOrderList);
      this.msNewOrderService.process(request);
    });
  }

  @Test
  public void invalidStockNumber(){
    assertThrows(IllegalArgumentException.class, () ->{
      // key -> stock id, value -> quantity
      // stock does not exist
      List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1","P0",2));

      NewOrderRequest request = getNewOrderRequest("W0","D0","C0", productToOrderList);
      this.msNewOrderService.process(request);
    });
  }

  @Test
  public void invalidStockQuantity(){
    int quantity = MsNewOrderService.MAX_RETRIES * StockData.INCREASE_QUANTITY + 2;
    assertThrows(MsTransactionException.class, () ->{
      // key -> stock id, value -> quantity
      List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1","P1",quantity));

      NewOrderRequest request = getNewOrderRequest("W0","D0","C0", productToOrderList);
      this.msNewOrderService.process(request);
    });

    // initial stock quantity was 2, and after 5 retries each adding 10, the value should be 52
    assertEquals(quantity,this.msDataRoot.getStocks().get("W1P1").getQuantity());
  }

  @Test
  public void processingNewOrderConcurrently() throws InterruptedException {
    adjustDefaults(5,10000);

    // key -> stock id, value -> quantity
    List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1", "P1", 2));
    NewOrderRequest request = getNewOrderRequest("W0","D0","C0", productToOrderList);
    int concurrentRequests = 5000;
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    for(int i = 0 ; i < concurrentRequests; i++){
      executorService.execute(() -> this.msNewOrderService.process(request));
    }

    this.shutdownAndAwaitTermination(executorService);
    assertEquals(2,this.msDataRoot.getStocks().get("W1P1").getQuantity());
  }

  void shutdownAndAwaitTermination(ExecutorService pool) {
    pool.shutdown(); // Disable new tasks from being submitted
    try {
      // Wait a while for existing tasks to terminate
      if (!pool.awaitTermination(60, TimeUnit.MINUTES)) {
        pool.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if (!pool.awaitTermination(60, TimeUnit.SECONDS))
          System.err.println("Pool did not terminate");
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      pool.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }

  @Test
  public void processingPersistsNewOrder() {

    // key -> stock id, value -> quantity
    List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1","P1",2),
            new ProductToOrder("W2", "P6",4),
            new ProductToOrder("W0","P8",5),
            new ProductToOrder("W3","P1",1));

    NewOrderRequest request = getNewOrderRequest("W0","D0","C0", productToOrderList);
    this.msNewOrderService.process(request);

    assertEquals(21,this.msDataRoot.getOrders().size());
  }



  @Test
  public void testRetryMechanism() {

    // key -> stock id, value -> quantity
    List<ProductToOrder> productToOrderList = List.of(new ProductToOrder("W1","P1",2),
            new ProductToOrder("W2", "P6",4),
            new ProductToOrder("W0","P8",11),
            new ProductToOrder("W3","P1",1));

    NewOrderRequest request = this.getNewOrderRequest("W0","D0","C0",productToOrderList);
    this.msNewOrderService.process(request);

    assertEquals(21,this.msDataRoot.getOrders().size());
  }

  private NewOrderRequest getNewOrderRequest(String warehouseId, String districtId, String customerId, List<ProductToOrder> productToOrderList) {
    NewOrderRequest request = new NewOrderRequest();
    request.setWarehouseId(warehouseId);
    request.setDistrictId(districtId);
    request.setCustomerId(customerId);

    List<NewOrderRequestItem> items = new ArrayList<>();
    for (ProductToOrder productsToOrder : productToOrderList) {
      String supplyingWarehouseId = productsToOrder.warehouseId;
      String productId = productsToOrder.productId;
      NewOrderRequestItem item = new NewOrderRequestItem();
      item.setProductId(productId);
      item.setQuantity(productsToOrder.quantity);
      item.setSupplyingWarehouseId(supplyingWarehouseId);
      items.add(item);
    }
    request.setItems(items);
    return request;
  }
}
