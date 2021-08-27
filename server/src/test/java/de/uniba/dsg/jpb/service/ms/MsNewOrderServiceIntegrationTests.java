package de.uniba.dsg.jpb.service.ms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.Find;
import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.jpb.data.transfer.messages.NewOrderResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MsNewOrderServiceIntegrationTests extends MicroStreamServiceTest {

  private DataManager dataManager;
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
  private int itemCount;
  private double preTaxTotal;

  @BeforeEach
  public void setUp() {
    populateStorage(new JpaDataGenerator(2, 1, 10, 10, 1_000, new BCryptPasswordEncoder()));
    dataManager = dataManager();
    request = new NewOrderRequest();

    dataManager.read(
        (root) -> {
          List<WarehouseData> warehouses = root.findAllWarehouses();
          WarehouseData warehouse = warehouses.get(0);
          warehouseId = warehouse.getId();
          warehouseSalesTax = warehouse.getSalesTax();

          DistrictData district = warehouse.getDistricts().get(0);
          districtId = district.getId();
          districtSalesTax = district.getSalesTax();
          districtOrderCount = district.getOrders().size();

          CustomerData customer = district.getCustomers().get(0);
          customerId = customer.getId();
          customerLastName = customer.getLastName();
          customerCredit = customer.getCredit();
          customerDiscount = customer.getDiscount();
          customerOrderCount = customer.getOrders().size();

          List<ProductData> products = root.findAllProducts();
          List<String> productIds =
              products.stream().map(ProductData::getId).collect(Collectors.toList());
          List<String> warehouseIds =
              warehouses.stream().map(WarehouseData::getId).collect(Collectors.toList());

          request = new NewOrderRequest();
          request.setWarehouseId(warehouse.getId());
          request.setDistrictId(district.getId());
          request.setCustomerId(customer.getId());
          List<NewOrderRequestItem> items = new ArrayList<>();
          itemCount = ThreadLocalRandom.current().nextInt(5, 16);
          for (int i = 0; i < itemCount; i++) {
            NewOrderRequestItem item = new NewOrderRequestItem();
            item.setProductId(
                productIds.get(ThreadLocalRandom.current().nextInt(productIds.size())));
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
        });
    dataManager = closeGivenCreateNewDataManager(dataManager);

    newOrderService = new MsNewOrderService(dataManager);
  }

  @Test
  public void processingPersistsNewOrder() {
    NewOrderResponse res = newOrderService.process(request);

    dataManager = closeGivenCreateNewDataManager(dataManager);
    dataManager.read(
        (root) -> {
          WarehouseData warehouse = Find.warehouseById(warehouseId, root.findAllWarehouses());
          DistrictData district = Find.districtById(districtId, warehouse);
          assertEquals(districtOrderCount + 1, district.getOrders().size());
          CustomerData customer = Find.customerById(customerId, district);
          assertEquals(customerOrderCount + 1, customer.getOrders().size());
          assertNotNull(
              customer.getOrders().stream()
                  .filter(o -> o.getId().equals(res.getOrderId()))
                  .findAny()
                  .orElse(null));
        });
  }

  @Test
  public void processingReturnsExpectedValues() {
    NewOrderResponse res = newOrderService.process(request);

    assertEquals(warehouseId, res.getWarehouseId());
    assertEquals(districtId, res.getDistrictId());
    assertEquals(customerId, res.getCustomerId());
    assertEquals(request.getItems().size(), res.getOrderItems().size());
    dataManager = closeGivenCreateNewDataManager(dataManager);
    dataManager.read(
        (root) -> {
          OrderData order =
              Find.orderById(
                  res.getOrderId(),
                  Find.districtById(
                      districtId, Find.warehouseById(warehouseId, root.findAllWarehouses())));

          assertEquals(order.getEntryDate(), res.getOrderTimestamp());
        });
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
    dataManager.close();
    clearStorage();
  }
}
