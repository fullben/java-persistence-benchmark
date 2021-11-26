package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.CustomerData;
import de.uniba.dsg.wss.data.model.DistrictData;
import de.uniba.dsg.wss.data.model.EmployeeData;
import de.uniba.dsg.wss.data.model.OrderData;
import de.uniba.dsg.wss.data.model.OrderItemData;
import de.uniba.dsg.wss.data.model.PaymentData;
import de.uniba.dsg.wss.data.model.ProductData;
import de.uniba.dsg.wss.data.model.StockData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jacis.container.JacisContainer;
import org.jacis.store.JacisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Can be used to write a wholesale supplier data model to MicroStream-based storage via the JACIS
 * stores.
 *
 * @author Benedikt Full
 */
@Component
public class MsDataWriter
    implements DataWriter<ProductData, WarehouseData, EmployeeData, CarrierData> {

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
  private final JacisStore<String, PaymentData> paymentStore;

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
      JacisStore<String, OrderItemData> orderItemStore,
      JacisStore<String, PaymentData> paymentStore) {
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
    this.paymentStore = paymentStore;
  }

  @Override
  public void write(DataModel<ProductData, WarehouseData, EmployeeData, CarrierData> model) {
    Stopwatch stopwatch = new Stopwatch().start();
    if (!(model instanceof MsDataModel)) {
      throw new IllegalArgumentException(
          "Expected instance of "
              + MsDataModel.class.getName()
              + ", but got "
              + (model == null ? null : model.getClass().getName()));
    }
    MsDataModel msDataModel = (MsDataModel) model;
    container.withLocalTx(
        () -> {
          carrierStore.update(msDataModel.getCarriers(), CarrierData::getId);
          productStore.update(msDataModel.getProducts(), ProductData::getId);
          warehouseStore.update(msDataModel.getWarehouses(), WarehouseData::getId);
          stockStore.update(msDataModel.getStocks(), StockData::getId);
          districtStore.update(msDataModel.getDistricts(), DistrictData::getId);
          employeeStore.update(msDataModel.getEmployees(), EmployeeData::getId);
          customerStore.update(msDataModel.getCustomers(), CustomerData::getId);
          orderStore.update(msDataModel.getOrders(), OrderData::getId);
          orderItemStore.update(msDataModel.getOrderItems(), OrderItemData::getId);
          paymentStore.update(msDataModel.getPayments(), PaymentData::getId);
        });
    stopwatch.stop();
    LOG.info("Wrote model data to MicroStream storage, took {}", stopwatch.getDuration());
  }
}
