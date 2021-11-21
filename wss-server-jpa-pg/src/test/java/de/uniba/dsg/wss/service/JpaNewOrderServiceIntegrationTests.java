package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.uniba.dsg.wss.data.access.CarrierRepository;
import de.uniba.dsg.wss.data.access.CustomerRepository;
import de.uniba.dsg.wss.data.access.ProductRepository;
import de.uniba.dsg.wss.data.access.StockRepository;
import de.uniba.dsg.wss.data.access.WarehouseRepository;
import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.data.gen.JpaDataConverter;
import de.uniba.dsg.wss.data.model.CustomerEntity;
import de.uniba.dsg.wss.data.model.DistrictEntity;
import de.uniba.dsg.wss.data.model.OrderEntity;
import de.uniba.dsg.wss.data.model.ProductEntity;
import de.uniba.dsg.wss.data.model.WarehouseEntity;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequest;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderRequestItem;
import de.uniba.dsg.wss.data.transfer.messages.NewOrderResponse;
import de.uniba.dsg.wss.data.access.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DataJpaTest
public class JpaNewOrderServiceIntegrationTests {

  @Autowired private WarehouseRepository warehouseRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private CarrierRepository carrierRepository;
  @Autowired private StockRepository stockRepository;
  @Autowired private CustomerRepository customerRepository;
  private JpaNewOrderService newOrderService;
  private NewOrderRequest request;
  private WarehouseEntity warehouse;
  private DistrictEntity district;
  private double warehouseSalesTax;
  private double districtSalesTax;
  private CustomerEntity customer;
  private String customerLastName;
  private String customerCredit;
  private double customerDiscount;
  private long totalOrderCount;
  private int itemCount;
  private double preTaxTotal;

  @BeforeEach
  public void setUp() {
    DataGenerator generator = new DataGenerator(2, 1, 10, 10, 1_000, new BCryptPasswordEncoder());
    generator.generate();
    JpaDataConverter converter = new JpaDataConverter();
    converter.convert(generator);

    productRepository.saveAll(converter.getProducts());
    carrierRepository.saveAll(converter.getCarriers());
    warehouseRepository.saveAll(converter.getWarehouses());

    warehouse = warehouseRepository.findAll().get(0);
    district = warehouse.getDistricts().get(0);
    customer = district.getCustomers().get(0);
    warehouseSalesTax = warehouse.getSalesTax();
    districtSalesTax = district.getSalesTax();
    customerLastName = customer.getLastName();
    customerCredit = customer.getCredit();
    customerDiscount = customer.getDiscount();
    totalOrderCount = orderRepository.count();

    List<ProductEntity> products = productRepository.findAll();
    List<String> productIds =
        products.stream().map(ProductEntity::getId).collect(Collectors.toList());
    List<String> warehouseIds =
        warehouseRepository.findAll().stream()
            .map(WarehouseEntity::getId)
            .collect(Collectors.toList());

    request = new NewOrderRequest();
    request.setWarehouseId(warehouse.getId());
    request.setDistrictId(district.getId());
    request.setCustomerId(customer.getId());
    List<NewOrderRequestItem> items = new ArrayList<>();
    itemCount = ThreadLocalRandom.current().nextInt(5, 16);
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
        new JpaNewOrderService(
            warehouseRepository,
            productRepository,
            stockRepository,
            orderRepository,
            customerRepository);
  }

  @Test
  public void processingPersistsNewOrder() {
    NewOrderResponse res = newOrderService.process(request);

    assertEquals(totalOrderCount + 1, orderRepository.count());
    assertNotNull(orderRepository.findById(res.getOrderId()).get());
  }

  @Test
  public void processingReturnsExpectedValues() {
    NewOrderResponse res = newOrderService.process(request);

    assertEquals(warehouse.getId(), res.getWarehouseId());
    assertEquals(district.getId(), res.getDistrictId());
    assertEquals(customer.getId(), res.getCustomerId());
    assertEquals(request.getItems().size(), res.getOrderItems().size());
    OrderEntity order = orderRepository.getById(res.getOrderId());
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
    warehouseRepository.deleteAll();
    productRepository.deleteAll();
    carrierRepository.deleteAll();
  }
}
