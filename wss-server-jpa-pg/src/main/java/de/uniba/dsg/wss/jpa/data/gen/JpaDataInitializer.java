package de.uniba.dsg.wss.jpa.data.gen;

import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.data.gen.DataInitializer;
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

  private final JpaDataWriter databaseWriter;

  @Autowired
  public JpaDataInitializer(
      Environment environment, PasswordEncoder passwordEncoder, JpaDataWriter databaseWriter) {
    super(environment, passwordEncoder);
    this.databaseWriter = databaseWriter;
  }

  @Override
  public void run(String... args) {
    DataGenerator generator =
        new DataGenerator(getModelWarehouseCount(), isFullScaleModel(), getPasswordEncoder());
    generator.generate();
    JpaDataConverter converter = new JpaDataConverter(generator);
    databaseWriter.writeAll(converter);
  }
}
