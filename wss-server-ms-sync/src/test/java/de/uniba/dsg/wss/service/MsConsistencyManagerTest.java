package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import de.uniba.dsg.wss.data.access.DataConsistencyManager;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MsConsistencyManagerTest extends MicroStreamServiceTest {

  @Autowired private DataConsistencyManager consistencyManager;
  @Autowired private MsDataRoot dataRoot;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
  }

  @Test
  public void checkDifferentCustomerDataObjectsAfterStoring() {
    String customerId = "C0";
    CustomerData originalCustomer = this.dataRoot.getCustomers().get(customerId);
    WarehouseData warehouse = this.dataRoot.getWarehouses().get("W0");
    DistrictData district = warehouse.getDistricts().get("D0");

    CustomerData copy =
        this.consistencyManager.storePaymentAndUpdateDependentObjects(
            warehouse, district, originalCustomer, originalCustomer.getPaymentRefs().get(0));
    // important that we get a copy of the customer object
    assertNotEquals(originalCustomer.hashCode(), copy.hashCode());
  }

  @Test
  public void checkStockThreshold() {
    int count =
        this.consistencyManager.countStockEntriesLowerThanThreshold(
            generateStockData(List.of("W0P0", "W0P4", "W4P4", "W2P4", "W4P8")), 8);
    assertEquals(3, count);
  }

  private List<StockData> generateStockData(List<String> stockIds) {
    return stockIds.stream().map(id -> dataRoot.getStocks().get(id)).collect(Collectors.toList());
  }
}
