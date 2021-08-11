package de.uniba.dsg.jpb.server;

import de.uniba.dsg.jpb.server.data.access.ms.DataRoot;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jpb.ms")
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsPersistenceConfiguration {

  private String storageDir;

  public MsPersistenceConfiguration() {
    storageDir = null;
  }

  @Bean
  public EmbeddedStorageManager embeddedStorageManager() {
    EmbeddedStorageFoundation<?> foundation =
        EmbeddedStorageConfiguration.Builder()
            .setStorageDirectory(storageDir)
            .setChannelCount(Integer.highestOneBit(Runtime.getRuntime().availableProcessors() - 1))
            .createEmbeddedStorageFoundation();
    return foundation.createEmbeddedStorageManager().start();
  }

  @Bean
  public DataRoot dataRoot(EmbeddedStorageManager embeddedStorageManager) {
    DataRoot root = (DataRoot) embeddedStorageManager.root();
    if (root == null) {
      root = new DataRoot();
      embeddedStorageManager.setRoot(root);
    }
    root.setStorageManager(embeddedStorageManager);
    return root;
  }

  public String getStorageDir() {
    return storageDir;
  }

  public void setStorageDir(String storageDir) {
    this.storageDir = storageDir;
  }
}
