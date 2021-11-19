package de.uniba.dsg.wss.ms.data.gen;

import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.data.gen.DataInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * If the server is launched in MS persistence mode, this initializer generates a data model based
 * on the associated configuration properties and writes it to the configured MicroStream storage.
 *
 * @author Benedikt Full
 */
@Component
@ConditionalOnExpression("'${jpb.persistence.mode}' == 'ms' and '${jpb.model.initialize}'")
public class MsDataInitializer extends DataInitializer {

  private static final Logger LOG = LogManager.getLogger(MsDataInitializer.class);
  private final MsDataWriter dataWriter;

  public MsDataInitializer(
      Environment environment, PasswordEncoder passwordEncoder, MsDataWriter dataWriter) {
    super(environment, passwordEncoder);
    this.dataWriter = dataWriter;
  }

  @Override
  public void run(String... args) {
    LOG.info("Beginning model data generation");
    DataGenerator generator =
        new DataGenerator(getModelWarehouseCount(), isFullScaleModel(), getPasswordEncoder());
    generator.generate();
    MsDataConverter converter = new MsDataConverter(generator);
    converter.convert();
    dataWriter.writeAll(converter);
  }
}
