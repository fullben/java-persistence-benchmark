package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.commons.Stopwatch;
import de.uniba.dsg.wss.data.model.CarrierData;
import de.uniba.dsg.wss.data.model.EmployeeData;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import de.uniba.dsg.wss.data.model.ProductData;
import de.uniba.dsg.wss.data.model.WarehouseData;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Can be used to write a wholesale supplier data model to MicroStream-based storage via the JACIS
 * stores.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 */
@Component
public class MsDataWriter
    implements DataWriter<ProductData, WarehouseData, EmployeeData, CarrierData> {

  private static final Logger LOG = LogManager.getLogger(MsDataWriter.class);
  private final EmbeddedStorageManager storageManager;
  private final MsDataRoot msDataRoot;

  @Autowired
  public MsDataWriter(EmbeddedStorageManager storageManager, MsDataRoot dataRoot) {
    this.storageManager = storageManager;
    this.msDataRoot = dataRoot;
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
    MsDataModel msModel = (MsDataModel) model;
    msDataRoot.getWarehouses().putAll(msModel.getIdsToWarehouses());
    msDataRoot.getEmployees().putAll(msModel.getIdsToEmployees());
    msDataRoot.getCustomers().putAll(msModel.getIdsToCustomers());
    msDataRoot.getStocks().putAll(msModel.getIdsToStocks());
    msDataRoot.getOrders().putAll(msModel.getIdsToOrders());
    msDataRoot.getCarriers().putAll(msModel.getIdsToCarriers());
    msDataRoot.getProducts().putAll(msModel.getIdsToProducts());
    storageManager.setRoot(msDataRoot);
    storageManager.storeRoot();
    stopwatch.stop();
    LOG.info("Wrote model data to MicroStream storage, took {}", stopwatch.getDuration());
  }
}
