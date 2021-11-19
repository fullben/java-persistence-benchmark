package de.uniba.dsg.wss.ms.data.gen;

import de.uniba.dsg.wss.data.gen.DataWriter;
import de.uniba.dsg.wss.ms.data.model.CarrierData;
import de.uniba.dsg.wss.ms.data.model.CustomerData;
import de.uniba.dsg.wss.ms.data.model.DistrictData;
import de.uniba.dsg.wss.ms.data.model.EmployeeData;
import de.uniba.dsg.wss.ms.data.model.OrderData;
import de.uniba.dsg.wss.ms.data.model.OrderItemData;
import de.uniba.dsg.wss.ms.data.model.ProductData;
import de.uniba.dsg.wss.ms.data.model.StockData;
import de.uniba.dsg.wss.ms.data.model.WarehouseData;
import de.uniba.dsg.wss.util.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jacis.container.JacisContainer;
import org.jacis.store.JacisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Can be used to write a wholesale supplier data model to MicroStream-based storage via the JACIS
 * stores.
 *
 * @author Benedikt Full
 */
@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsDataWriter implements DataWriter<MsDataConverter> {

  private static final Logger LOG = LogManager.getLogger(MsDataWriter.class);
  private final JacisContainer container;
  private final JacisStore<String, CarrierData> carrierStore;
  private final JacisStore<String, ProductData> productStore;
  private final JacisStore<String, WarehouseData> warehouseStore;
  private final JacisStore<String, DistrictData> districtStore;
  private final JacisStore<String, EmployeeData> employeeStore;
  private final JacisStore<String, StockData> stockStore;
  private final JacisStore<String, CustomerData> customerStore;
  private final JacisStore<String, OrderData> orderStore;
  private final JacisStore<String, OrderItemData> orderItemStore;

  @Autowired
  public MsDataWriter(
      JacisContainer container,
      JacisStore<String, CarrierData> carrierStore,
      JacisStore<String, ProductData> productStore,
      JacisStore<String, WarehouseData> warehouseStore,
      JacisStore<String, DistrictData> districtStore,
      JacisStore<String, EmployeeData> employeeStore,
      JacisStore<String, StockData> stockStore,
      JacisStore<String, CustomerData> customerStore,
      JacisStore<String, OrderData> orderStore,
      JacisStore<String, OrderItemData> orderItemStore) {
    this.container = container;
    this.carrierStore = carrierStore;
    this.productStore = productStore;
    this.warehouseStore = warehouseStore;
    this.districtStore = districtStore;
    this.employeeStore = employeeStore;
    this.stockStore = stockStore;
    this.customerStore = customerStore;
    this.orderStore = orderStore;
    this.orderItemStore = orderItemStore;
  }

  @Override
  public void writeAll(MsDataConverter converter) {
    Stopwatch stopwatch = new Stopwatch(true);
    container.withLocalTx(
        () -> {
          carrierStore.update(converter.getCarriers(), CarrierData::getId);
          productStore.update(converter.getProducts(), ProductData::getId);
          warehouseStore.update(converter.getWarehouses(), WarehouseData::getId);
          stockStore.update(converter.getStocks(), StockData::getId);
          districtStore.update(converter.getDistricts(), DistrictData::getId);
          employeeStore.update(converter.getEmployees(), EmployeeData::getId);
          customerStore.update(converter.getCustomers(), CustomerData::getId);
          orderStore.update(converter.getOrders(), OrderData::getId);
          orderItemStore.update(converter.getOrderItems(), OrderItemData::getId);
        });
    stopwatch.stop();
    LOG.info("Wrote model data to MicroStream storage, took {}", stopwatch.getDuration());
  }
}
