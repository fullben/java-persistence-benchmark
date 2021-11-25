package de.uniba.dsg.wss.data.gen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This initializer generates a data model based on the associated configuration properties and
 * writes it to the configured JPA-based persistence solution.
 *
 * @author Benedikt Full
 */
@Component
@ConditionalOnProperty(name = "jpb.model.initialize", havingValue = "true")
public class JpaDataInitializer extends DataInitializer {

  private static final Logger LOG = LogManager.getLogger(JpaDataInitializer.class);
  private final JpaDataWriter databaseWriter;

  @Autowired
  public JpaDataInitializer(
      Environment environment, PasswordEncoder passwordEncoder, JpaDataWriter databaseWriter) {
    super(environment, passwordEncoder);
    this.databaseWriter = databaseWriter;
  }

  @Override
  public void initializePersistentData() {
    LOG.info("Beginning model data generation");
    DataGenerator generator = generateData();
    JpaDataConverter converter = new JpaDataConverter();
    converter.convert(generator);
    databaseWriter.writeAll(converter);
  }
}
