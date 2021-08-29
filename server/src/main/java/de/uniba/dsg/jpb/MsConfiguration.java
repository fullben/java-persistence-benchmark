package de.uniba.dsg.jpb;

import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.DataRoot;
import de.uniba.dsg.jpb.data.access.ms.FieldEvaluator;
import de.uniba.dsg.jpb.util.Stopwatch;
import one.microstream.afs.nio.types.NioFileSystem;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import one.microstream.storage.types.Storage;
import one.microstream.storage.types.StorageChannelCountProvider;
import one.microstream.storage.types.StorageConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * @author Benedikt Full
 */
@Configuration
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class MsConfiguration {

  private static final Logger LOG = LogManager.getLogger(MsConfiguration.class);
  private static final String STARTUP_BEHAVIOR_KEY = "jpb.ms.storage.startup";
  private static final String STARTUP_BEHAVIOR_LOAD = "load";
  private static final String STARTUP_BEHAVIOR_CLEAR = "clear";

  private final Environment environment;

  @Autowired
  public MsConfiguration(Environment environment) {
    this.environment = environment;
  }

  @Bean
  public EmbeddedStorageManager embeddedStorageManager() {
    String clearStorageValue = environment.getProperty(STARTUP_BEHAVIOR_KEY);
    boolean clearStorage;
    if (clearStorageValue == null || clearStorageValue.isBlank()) {
      clearStorage = false;
    } else {
      if (clearStorageValue.equals(STARTUP_BEHAVIOR_LOAD)) {
        clearStorage = false;
      } else if (clearStorageValue.equals(STARTUP_BEHAVIOR_CLEAR)) {
        clearStorage = true;
      } else {
        throw new IllegalArgumentException(
            "Invalid value for property " + STARTUP_BEHAVIOR_KEY + ": " + clearStorageValue);
      }
    }

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
                            .setDirectory(
                                fileSystem.ensureDirectoryPath(
                                    environment.getRequiredProperty("jpb.ms.storage.dir")))
                            .createFileProvider())
                    .setChannelCountProvider(
                        StorageChannelCountProvider.New(
                            Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1)))
                    .createConfiguration());

    EmbeddedStorageManager storageManager =
        foundation
            .onConnectionFoundation(f -> f.setReferenceFieldEagerEvaluator(new FieldEvaluator()))
            .createEmbeddedStorageManager();

    Stopwatch stopwatch = new Stopwatch(true);
    storageManager.start();
    stopwatch.stop();
    LOG.info("Started MicroStream storage manager, took {}", stopwatch.getDuration());

    if (clearStorage) {
      storageManager.setRoot(new DataRoot());
      storageManager.storeRoot();
      LOG.info("Cleared MicroStream persistent storage");
    }

    return storageManager;
  }

  @Bean
  public DataManager dataManager(EmbeddedStorageManager storageManager) {
    return new DataManager(storageManager);
  }
}
