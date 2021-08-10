package de.uniba.dsg.jpb.server;

import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "microstream")
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsConfig {

  private String storageDir;

  public MsConfig() {
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

  public String getStorageDir() {
    return storageDir;
  }

  public void setStorageDir(String storageDir) {
    this.storageDir = storageDir;
  }
}
