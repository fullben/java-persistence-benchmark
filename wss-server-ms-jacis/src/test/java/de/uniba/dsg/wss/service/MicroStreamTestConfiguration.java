package de.uniba.dsg.wss.service;

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
import de.uniba.dsg.wss.data.gen.MsDataWriter;
import one.microstream.afs.nio.types.NioFileSystem;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import one.microstream.storage.types.Storage;
import one.microstream.storage.types.StorageChannelCountProvider;
import one.microstream.storage.types.StorageConfiguration;
import org.jacis.container.JacisContainer;
import org.jacis.container.JacisObjectTypeSpec;
import org.jacis.extension.persistence.microstream.MicrostreamPersistenceAdapter;
import org.jacis.extension.persistence.microstream.MicrostreamStorage;
import org.jacis.plugin.objectadapter.cloning.JacisCloningObjectAdapter;
import org.jacis.store.JacisStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MicroStreamTestConfiguration {

  @Bean
  public EmbeddedStorageManager embeddedStorageManager() {
    NioFileSystem fileSystem = NioFileSystem.New();
    EmbeddedStorageFoundation<?> foundation =
        EmbeddedStorageFoundation.New()
            .setConfiguration(
                StorageConfiguration.Builder()
                    .setHousekeepingController(Storage.HousekeepingController(1_000, 1_000_000))
                    .setDataFileEvaluator(
                        Storage.DataFileEvaluator(1024 * 1024, 1024 * 1024 * 8, 0.75))
                    .setEntityCacheEvaluator(
                        Storage.EntityCacheEvaluator(86_400_000, 1_000_000_000))
                    .setStorageFileProvider(
                        Storage.FileProviderBuilder(fileSystem)
                            .setDirectory(fileSystem.ensureDirectoryPath("test-storage"))
                            .createFileProvider())
                    .setChannelCountProvider(
                        StorageChannelCountProvider.New(
                            Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1)))
                    .createConfiguration());

    EmbeddedStorageManager storageManager = foundation.createEmbeddedStorageManager();

    storageManager.start();
    storageManager.setRoot(null);
    storageManager.storeRoot();

    return storageManager;
  }

  @Bean
  public MicrostreamStorage microstreamStorage(EmbeddedStorageManager storageManager) {
    return new MicrostreamStorage(storageManager);
  }

  @Bean
  public JacisContainer jacisContainer() {
    return new JacisContainer();
  }

  @Bean
  public JacisStore<String, CarrierData> carrierStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, CarrierData, CarrierData> carrierTypeSpec =
        new JacisObjectTypeSpec<>(
            String.class, CarrierData.class, new JacisCloningObjectAdapter<>());
    carrierTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(carrierTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, ProductData> productStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, ProductData, ProductData> productTypeSpec =
        new JacisObjectTypeSpec<>(
            String.class, ProductData.class, new JacisCloningObjectAdapter<>());
    productTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(productTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, WarehouseData> warehouseStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, WarehouseData, WarehouseData> warehouseTypeSpec =
        new JacisObjectTypeSpec<>(
            String.class, WarehouseData.class, new JacisCloningObjectAdapter<>());
    warehouseTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(warehouseTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, StockData> stockStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, StockData, StockData> stockTypeSpec =
        new JacisObjectTypeSpec<>(String.class, StockData.class, new JacisCloningObjectAdapter<>());
    stockTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(stockTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, DistrictData> districtStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, DistrictData, DistrictData> districtTypeSpec =
        new JacisObjectTypeSpec<>(
            String.class, DistrictData.class, new JacisCloningObjectAdapter<>());
    districtTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(districtTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, CustomerData> customerStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, CustomerData, CustomerData> customerTypeSpec =
        new JacisObjectTypeSpec<>(
            String.class, CustomerData.class, new JacisCloningObjectAdapter<>());
    customerTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(customerTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, OrderData> orderStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, OrderData, OrderData> orderTypeSpec =
        new JacisObjectTypeSpec<>(String.class, OrderData.class, new JacisCloningObjectAdapter<>());
    orderTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(orderTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, OrderItemData> orderItemStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, OrderItemData, OrderItemData> orderItemTypeSpec =
        new JacisObjectTypeSpec<>(
            String.class, OrderItemData.class, new JacisCloningObjectAdapter<>());
    orderItemTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(orderItemTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, PaymentData> paymentStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, PaymentData, PaymentData> paymentTypeSpec =
        new JacisObjectTypeSpec<>(
            String.class, PaymentData.class, new JacisCloningObjectAdapter<>());
    paymentTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(paymentTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, EmployeeData> employeeStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, EmployeeData, EmployeeData> employeeTypeSpec =
        new JacisObjectTypeSpec<>(
            String.class, EmployeeData.class, new JacisCloningObjectAdapter<>());
    employeeTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(employeeTypeSpec).getStore();
  }

  @Bean
  public MsDataWriter dataWriter(
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
    return new MsDataWriter(
        container,
        carrierStore,
        productStore,
        warehouseStore,
        districtStore,
        employeeStore,
        stockStore,
        customerStore,
        orderStore,
        orderItemStore);
  }
}
