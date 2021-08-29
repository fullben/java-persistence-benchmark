package de.uniba.dsg.jpb.data.gen.ms;

import de.uniba.dsg.jpb.data.access.ms.DataRoot;
import de.uniba.dsg.jpb.data.gen.DataProvider;
import de.uniba.dsg.jpb.data.gen.DataWriter;
import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Can be used to write a wholesale supplier data model to MicroStream-based storage.
 *
 * <p>Note that instances are {@link AutoCloseable}, as they hold a reference to the object graph
 * potentially managed by a MicroStream storage manager. Calling the {@link #close()} method will
 * remove this internal reference, thus allowing the graph to be garbage collected if no other
 * object maintains a reference to it. This is necessary as this class is configured to be a
 * <i>Spring component</i>, meaning a managed instance will exist at runtime.
 *
 * @author Benedikt Full
 */
@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsDataWriter
    implements DataWriter<WarehouseData, EmployeeData, ProductData, CarrierData>, AutoCloseable {

  private DataRoot dataRoot;
  private boolean closed;

  @Autowired
  public MsDataWriter(EmbeddedStorageManager storageManager) {
    this.dataRoot = (DataRoot) storageManager.root();
    closed = false;
  }

  @Override
  public void writeAll(
      DataProvider<WarehouseData, EmployeeData, ProductData, CarrierData> dataProvider) {
    if (closed) {
      throw new IllegalStateException("Data writer is closed");
    }
    dataRoot.init(
        dataProvider.getProducts(),
        dataProvider.getCarriers(),
        dataProvider.getWarehouses(),
        dataProvider.getEmployees());
  }

  /**
   * Removes the internal reference to the {@link DataRoot} provided to the constructor and changes
   * the state of the instance to closed.
   */
  @Override
  public void close() {
    if (closed) {
      return;
    }
    dataRoot = null;
    closed = true;
  }
}
