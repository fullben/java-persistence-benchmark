package de.uniba.dsg.jpb.data.gen.ms;

import de.uniba.dsg.jpb.data.gen.DataWriter;
import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.StockData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
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
public class MsDataWriter implements DataWriter<JpaToMsConverter> {

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
  public void writeAll(JpaToMsConverter converter) {
    container.withLocalTx(
        () -> {
          converter.getCarriers().forEach(c -> carrierStore.update(c.getId(), c));
          converter.getProducts().forEach(p -> productStore.update(p.getId(), p));
          converter.getWarehouses().forEach(w -> warehouseStore.update(w.getId(), w));
          converter.getStocks().forEach(s -> stockStore.update(s.getId(), s));
          converter.getDistricts().forEach(d -> districtStore.update(d.getId(), d));
          converter.getEmployees().forEach(e -> employeeStore.update(e.getId(), e));
          converter.getCustomers().forEach(c -> customerStore.update(c.getId(), c));
          converter.getOrders().forEach(o -> orderStore.update(o.getId(), o));
          converter.getOrderItems().forEach(i -> orderItemStore.update(i.getId(), i));
        });
  }
}
