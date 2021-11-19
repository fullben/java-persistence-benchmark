package de.uniba.dsg.wss.ms;

import de.uniba.dsg.wss.data.model.ms.CarrierData;
import de.uniba.dsg.wss.data.model.ms.CustomerData;
import de.uniba.dsg.wss.data.model.ms.DistrictData;
import de.uniba.dsg.wss.data.model.ms.EmployeeData;
import de.uniba.dsg.wss.data.model.ms.OrderData;
import de.uniba.dsg.wss.data.model.ms.OrderItemData;
import de.uniba.dsg.wss.data.model.ms.PaymentData;
import de.uniba.dsg.wss.data.model.ms.ProductData;
import de.uniba.dsg.wss.data.model.ms.StockData;
import de.uniba.dsg.wss.data.model.ms.WarehouseData;
import de.uniba.dsg.wss.util.Stopwatch;
import one.microstream.afs.nio.types.NioFileSystem;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import one.microstream.storage.types.Storage;
import one.microstream.storage.types.StorageChannelCountProvider;
import one.microstream.storage.types.StorageConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jacis.container.JacisContainer;
import org.jacis.container.JacisObjectTypeSpec;
import org.jacis.extension.persistence.microstream.MicrostreamPersistenceAdapter;
import org.jacis.extension.persistence.microstream.MicrostreamStorage;
import org.jacis.store.JacisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configures all beans which are required for MicroStream-based persistence.
 *
 * <p>This includes many beans relevant for data access, for which the <a
 * href="https://github.com/JanWiemer/jacis">JACIS framework</a> is being used. This is due to the
 * fact that MicroStream itself is only a storage engine, which provides no support for
 * transactions.
 *
 * @author Benedikt Full
 */
@Configuration
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class MsConfiguration {

  private static final Logger LOG = LogManager.getLogger(MsConfiguration.class);

  private final Environment environment;

  @Autowired
  public MsConfiguration(Environment environment) {
    this.environment = environment;
  }

  @Bean
  public EmbeddedStorageManager embeddedStorageManager() {
    NioFileSystem fileSystem = NioFileSystem.New();
    EmbeddedStorageFoundation<?> foundation =
        EmbeddedStorageFoundation.New()
            .setConfiguration(
                StorageConfiguration.Builder()
                    .setHousekeepingController(Storage.HousekeepingController(1_000, 10_000_000))
                    .setDataFileEvaluator(
                        Storage.DataFileEvaluator(1024 * 1024, 1024 * 1024 * 8, 0.75))
                    .setEntityCacheEvaluator(
                        Storage.EntityCacheEvaluator(86_400_000, 1_000_000_000))
                    .setStorageFileProvider(
                        Storage.FileProviderBuilder(fileSystem)
                            .setDirectory(
                                fileSystem.ensureDirectoryPath(
                                    environment.getRequiredProperty("jpb.ms.storage.dir")))
                            .createFileProvider())
                    .setChannelCountProvider(
                        StorageChannelCountProvider.New(
                            Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1)))
                    .createConfiguration());

    EmbeddedStorageManager storageManager = foundation.createEmbeddedStorageManager();

    Stopwatch stopwatch = new Stopwatch(true);
    storageManager.start();
    stopwatch.stop();
    LOG.info("Started MicroStream storage manager, took {}", stopwatch.getDuration());

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
        new JacisObjectTypeSpec<>(String.class, CarrierData.class);
    carrierTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(carrierTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, ProductData> productStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, ProductData, ProductData> productTypeSpec =
        new JacisObjectTypeSpec<>(String.class, ProductData.class);
    productTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(productTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, WarehouseData> warehouseStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, WarehouseData, WarehouseData> warehouseTypeSpec =
        new JacisObjectTypeSpec<>(String.class, WarehouseData.class);
    warehouseTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(warehouseTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, StockData> stockStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, StockData, StockData> stockTypeSpec =
        new JacisObjectTypeSpec<>(String.class, StockData.class);
    stockTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(stockTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, DistrictData> districtStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, DistrictData, DistrictData> districtTypeSpec =
        new JacisObjectTypeSpec<>(String.class, DistrictData.class);
    districtTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(districtTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, CustomerData> customerStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, CustomerData, CustomerData> customerTypeSpec =
        new JacisObjectTypeSpec<>(String.class, CustomerData.class);
    customerTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(customerTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, OrderData> orderStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, OrderData, OrderData> orderTypeSpec =
        new JacisObjectTypeSpec<>(String.class, OrderData.class);
    orderTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(orderTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, OrderItemData> orderItemStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, OrderItemData, OrderItemData> orderItemTypeSpec =
        new JacisObjectTypeSpec<>(String.class, OrderItemData.class);
    orderItemTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(orderItemTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, PaymentData> paymentStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, PaymentData, PaymentData> paymentTypeSpec =
        new JacisObjectTypeSpec<>(String.class, PaymentData.class);
    paymentTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(paymentTypeSpec).getStore();
  }

  @Bean
  public JacisStore<String, EmployeeData> employeeStore(
      JacisContainer container, MicrostreamStorage storage) {
    JacisObjectTypeSpec<String, EmployeeData, EmployeeData> employeeTypeSpec =
        new JacisObjectTypeSpec<>(String.class, EmployeeData.class);
    employeeTypeSpec.setPersistenceAdapter(new MicrostreamPersistenceAdapter<>(storage));
    return container.createStore(employeeTypeSpec).getStore();
  }
}
