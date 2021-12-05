package de.uniba.dsg.wss;

import de.uniba.dsg.wss.data.gen.MsDataConverter;
import de.uniba.dsg.wss.data.gen.MsDataModel;
import de.uniba.dsg.wss.data.gen.TestDataGenerator;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import de.uniba.dsg.wss.service.MicroStreamTestConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(MicroStreamTestConfiguration.class)
public abstract class MicroStreamTest {

  @Autowired protected EmbeddedStorageManager storageManager;
  @Autowired protected MsDataRoot msDataRoot;

  public MicroStreamTest() {}

  public void prepareTestStorage() {

    MsDataModel dataModel = new MsDataConverter().convert(new TestDataGenerator().generate());

    // remove all data from data root - cannot instantiate another root object since then I get null
    // pointers in the logic classes
    // bean has to be the same object
    this.msDataRoot.getOrders().clear();
    this.msDataRoot.getEmployees().clear();
    this.msDataRoot.getWarehouses().clear();
    this.msDataRoot.getStocks().clear();
    this.msDataRoot.getCustomers().clear();
    this.msDataRoot.getCarriers().clear();
    this.msDataRoot.getProducts().clear();

    // add all data to data root :)
    this.msDataRoot.getWarehouses().putAll(dataModel.getIdsToWarehouses());
    this.msDataRoot.getStocks().putAll(dataModel.getIdsToStocks());
    this.msDataRoot.getCustomers().putAll(dataModel.getIdsToCustomers());
    this.msDataRoot.getOrders().putAll(dataModel.getIdsToOrders());
    this.msDataRoot.getCarriers().putAll(dataModel.getIdsToCarriers());
    this.msDataRoot.getProducts().putAll(dataModel.getIdsToProducts());
    this.msDataRoot.getEmployees().putAll(dataModel.getIdsToEmployees());

    this.storageManager.setRoot(msDataRoot);
    this.storageManager.storeRoot();
  }
}
