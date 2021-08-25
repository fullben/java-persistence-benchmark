package de.uniba.dsg.jpb.data.gen.ms;

import de.uniba.dsg.jpb.data.access.ms.DataRoot;
import de.uniba.dsg.jpb.data.gen.DataProvider;
import de.uniba.dsg.jpb.data.gen.DataWriter;
import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsDataWriter
    implements DataWriter<WarehouseData, EmployeeData, ProductData, CarrierData>, AutoCloseable {

  private DataRoot dataRoot;
  private boolean closed;

  public MsDataWriter(DataRoot dataRoot) {
    this.dataRoot = dataRoot;
    closed = false;
  }

  @Override
  public void writeAll(
      DataProvider<WarehouseData, EmployeeData, ProductData, CarrierData> dataProvider) {
    if (closed) {
      return;
    }
    dataRoot.init(
        dataProvider.getProducts(),
        dataProvider.getCarriers(),
        dataProvider.getWarehouses(),
        dataProvider.getEmployees());
  }

  @Override
  public void close() {
    if (closed) {
      return;
    }
    dataRoot = null;
    closed = true;
  }
}
