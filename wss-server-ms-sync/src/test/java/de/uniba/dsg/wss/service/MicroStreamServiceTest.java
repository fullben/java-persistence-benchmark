package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.gen.MsDataConverter;
import de.uniba.dsg.wss.data.gen.TestDataGenerator;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(MicroStreamTestConfiguration.class)
public abstract class MicroStreamServiceTest {

  @Autowired protected EmbeddedStorageManager storageManager;
  @Autowired protected MsDataRoot msDataRoot;

  public MicroStreamServiceTest() {}

  public void prepareTestStorage() {

    TestDataGenerator generator = new TestDataGenerator();
    generator.generate();
    MsDataConverter converter = new MsDataConverter();
    converter.convert(generator);

    // remove all data from data root - cannot instantiate another root object since then I get null
    // pointers in the logic classes
    // bean has to be the same object
    this.msDataRoot.getOrders().clear();
    this.msDataRoot.getEmployees().clear();
    this.msDataRoot.getWarehouses().clear();
    this.msDataRoot.getStocks().clear();
    this.msDataRoot.getCustomers().clear();
    this.msDataRoot.getCarriers().clear();

    // add all data to data root :)
    this.msDataRoot.getWarehouses().putAll(converter.getWarehouses());
    this.msDataRoot.getStocks().putAll(converter.getStocks());
    this.msDataRoot.getCustomers().putAll(converter.getCustomers());
    this.msDataRoot.getOrders().putAll(converter.getOrders());
    this.msDataRoot.getCarriers().putAll(converter.getCarriers());

    this.storageManager.setRoot(msDataRoot);
    this.storageManager.storeRoot();
  }
}
