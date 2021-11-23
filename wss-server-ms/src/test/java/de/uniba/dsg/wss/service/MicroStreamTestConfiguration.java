package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import one.microstream.afs.nio.types.NioFileSystem;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import one.microstream.storage.types.Storage;
import one.microstream.storage.types.StorageChannelCountProvider;
import one.microstream.storage.types.StorageConfiguration;
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
  public MsDataRoot createDataRoot(){
    return new MsDataRoot();
  }
}
