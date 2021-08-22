package de.uniba.dsg.jpb;

import de.uniba.dsg.jpb.data.access.ms.DataRoot;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsConfiguration {

  private final Environment environment;

  @Autowired
  public MsConfiguration(Environment environment) {
    this.environment = environment;
  }

  @Bean
  public EmbeddedStorageManager embeddedStorageManager() {
    EmbeddedStorageFoundation<?> foundation =
        EmbeddedStorageConfiguration.Builder()
            .setStorageDirectory(environment.getRequiredProperty("jpb.ms.storage-dir"))
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
}
