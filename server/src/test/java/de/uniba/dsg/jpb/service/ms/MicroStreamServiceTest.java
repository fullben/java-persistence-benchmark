package de.uniba.dsg.jpb.service.ms;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.DataRoot;
import de.uniba.dsg.jpb.data.access.ms.FieldEvaluator;
import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.data.gen.ms.JpaToMsConverter;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

public abstract class MicroStreamServiceTest {

  public EmbeddedStorageFoundation<?> storageFoundation() {
    return EmbeddedStorageConfiguration.Builder()
        .setStorageDirectory("test-storage")
        .setChannelCount(Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1))
        .createEmbeddedStorageFoundation()
        .onConnectionFoundation(f -> f.setReferenceFieldEagerEvaluator(new FieldEvaluator()));
  }

  public DataManager closeGivenCreateNewDataManager(DataManager old) {
    old.close();
    return dataManager();
  }

  public DataManager dataManager() {
    EmbeddedStorageManager storageManager = storageFoundation().start();
    DataManager dataManager = new DataManager((DataRoot) storageManager.root());
    dataManager.setStorageManager(storageManager);
    return dataManager;
  }

  public void populateStorage(JpaDataGenerator generator) {
    EmbeddedStorageManager storageManager = storageFoundation().start();
    DataRoot root = new DataRoot();

    generator.generate();
    JpaToMsConverter converter = new JpaToMsConverter(generator);
    converter.convert();
    root.init(
        converter.getProducts(),
        converter.getCarriers(),
        converter.getWarehouses(),
        converter.getEmployees());

    storageManager.setRoot(root);
    storageManager.storeRoot();
    if (!storageManager.shutdown()) {
      throw new IllegalStateException();
    }
  }

  public void clearStorage() {
    EmbeddedStorageManager storageManager = storageFoundation().start();
    DataRoot root = new DataRoot();
    storageManager.setRoot(root);
    storageManager.storeRoot();
    storageManager.shutdown();
  }
}
