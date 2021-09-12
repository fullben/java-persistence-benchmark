package de.uniba.dsg.wss.data.gen.ms;

import de.uniba.dsg.wss.data.gen.DataInitializer;
import de.uniba.dsg.wss.data.gen.jpa.JpaDataGenerator;
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
    JpaDataGenerator jpaDataGenerator = createJpaDataGenerator();
    LOG.info("Beginning model data generation");
    jpaDataGenerator.generate();
    JpaToMsConverter converter = new JpaToMsConverter(jpaDataGenerator);
    converter.convert();
    dataWriter.writeAll(converter);
  }
}
