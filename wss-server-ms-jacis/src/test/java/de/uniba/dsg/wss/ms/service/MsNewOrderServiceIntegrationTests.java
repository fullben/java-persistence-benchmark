package de.uniba.dsg.wss.ms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.wss.ms.data.gen.MsDataWriter;
import de.uniba.dsg.wss.ms.data.model.CustomerData;
import de.uniba.dsg.wss.ms.data.model.DistrictData;
import de.uniba.dsg.wss.ms.data.model.OrderData;
import de.uniba.dsg.wss.ms.data.model.OrderItemData;
import de.uniba.dsg.wss.ms.data.model.ProductData;
import de.uniba.dsg.wss.ms.data.model.StockData;
import de.uniba.dsg.wss.ms.data.model.WarehouseData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.jacis.container.JacisContainer;
import org.jacis.store.JacisStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MsNewOrderServiceIntegrationTests extends MicroStreamServiceTest {

  @Autowired private JacisContainer container;
  @Autowired private JacisStore<String, ProductData> productStore;
  @Autowired private JacisStore<String, WarehouseData> warehouseStore;
  @Autowired private JacisStore<String, StockData> stockStore;
  @Autowired private JacisStore<String, DistrictData> districtStore;
  @Autowired private JacisStore<String, CustomerData> customerStore;
  @Autowired private JacisStore<String, OrderData> orderStore;
  @Autowired private JacisStore<String, OrderItemData> orderItemStore;
  @Autowired private MsDataWriter dataWriter;
  private MsNewOrderService newOrderService;
  private NewOrderRequest request;
  private String warehouseId;
  private double warehouseSalesTax;
  private String districtId;
  private double districtSalesTax;
  private int districtOrderCount;
  private String customerId;
  private String customerLastName;
  private String customerCredit;
  private double customerDiscount;
  private int customerOrderCount;
  private double preTaxTotal;

  @BeforeEach
  public void setUp() {
    populateStorage(
        new DataGenerator(2, 1, 10, 10, 1_000, new BCryptPasswordEncoder()), dataWriter);
    request = new NewOrderRequest();

    List<WarehouseData> warehouses = warehouseStore.getAllReadOnly();
    WarehouseData warehouse = warehouses.get(0);
    warehouseId = warehouse.getId();
    warehouseSalesTax = warehouse.getSalesTax();

    DistrictData district =
        districtStore.getAllReadOnly(d -> d.getWarehouseId().equals(warehouseId)).get(0);
    districtId = district.getId();
    districtSalesTax = district.getSalesTax();
    districtOrderCount =
        (int) orderStore.streamReadOnly(o -> o.getDistrictId().equals(districtId)).count();

    CustomerData customer =
        customerStore
            .streamReadOnly(c -> c.getDistrictId().equals(districtId))
            .collect(Collectors.toList())
            .get(0);
    customerId = customer.getId();
    customerLastName = customer.getLastName();
    customerCredit = customer.getCredit();
    customerDiscount = customer.getDiscount();
    customerOrderCount =
        (int) orderStore.streamReadOnly(o -> o.getCustomerId().equals(customerId)).count();

    List<ProductData> products = productStore.getAllReadOnly();
    List<String> productIds =
        products.stream().map(ProductData::getId).collect(Collectors.toList());
    List<String> warehouseIds =
        warehouses.stream().map(WarehouseData::getId).collect(Collectors.toList());

    request = new NewOrderRequest();
    request.setWarehouseId(warehouse.getId());
    request.setDistrictId(district.getId());
    request.setCustomerId(customer.getId());
    List<NewOrderRequestItem> items = new ArrayList<>();
    int itemCount = ThreadLocalRandom.current().nextInt(5, 16);
    for (int i = 0; i < itemCount; i++) {
      NewOrderRequestItem item = new NewOrderRequestItem();
      item.setProductId(productIds.get(ThreadLocalRandom.current().nextInt(productIds.size())));
      item.setQuantity(ThreadLocalRandom.current().nextInt(10, 21));
      item.setSupplyingWarehouseId(
          warehouseIds.get(ThreadLocalRandom.current().nextInt(warehouseIds.size())));
      items.add(item);
    }
    request.setItems(items);

    preTaxTotal =
        request.getItems().stream()
            .mapToDouble(
                i ->
                    products.stream()
                            .filter(prod -> prod.getId().equals(i.getProductId()))
                            .findAny()
                            .orElseThrow(IllegalStateException::new)
                            .getPrice()
                        * i.getQuantity())
            .sum();

    newOrderService =
        new MsNewOrderService(
            container,
            warehouseStore,
            districtStore,
            stockStore,
            customerStore,
            orderStore,
            orderItemStore,
            productStore);
  }

  @Test
  public void processingPersistsNewOrder() {
    newOrderService.process(request);

    assertEquals(
        districtOrderCount + 1,
        orderStore.streamReadOnly(o -> o.getDistrictId().equals(districtId)).count());
    assertEquals(
        customerOrderCount + 1,
        orderStore.streamReadOnly(o -> o.getCustomerId().equals(customerId)).count());
  }

  @Test
  public void processingReturnsExpectedValues() {
    NewOrderResponse res = newOrderService.process(request);

    assertEquals(warehouseId, res.getWarehouseId());
    assertEquals(districtId, res.getDistrictId());
    assertEquals(customerId, res.getCustomerId());
    assertEquals(request.getItems().size(), res.getOrderItems().size());
    OrderData order = orderStore.getReadOnly(res.getOrderId());
    assertEquals(order.getEntryDate(), res.getOrderTimestamp());
    assertEquals(customerLastName, res.getCustomerLastName());
    assertEquals(customerCredit, res.getCustomerCredit());
    assertEquals(customerDiscount, res.getCustomerDiscount());
    assertEquals(warehouseSalesTax, res.getWarehouseSalesTax());
    assertEquals(districtSalesTax, res.getDistrictSalesTax());
    double total =
        Math.floor(
                ((preTaxTotal * (1 - customerDiscount) * (1 + warehouseSalesTax + districtSalesTax))
                    * 100))
            / 100;

    assertEquals(total, res.getTotalAmount());
  }

  @AfterEach
  public void tearDown() {
    container.clearAllStores();
  }
}
