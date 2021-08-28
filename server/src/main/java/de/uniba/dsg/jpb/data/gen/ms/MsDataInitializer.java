package de.uniba.dsg.jpb.data.gen.ms;

import de.uniba.dsg.jpb.data.gen.DataInitializer;
import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.util.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsDataInitializer extends DataInitializer {

  private static final Logger LOG = LogManager.getLogger(MsDataInitializer.class);
  private final MsDataWriter databaseWriter;

  @Autowired
  public MsDataInitializer(
      Environment environment, PasswordEncoder passwordEncoder, MsDataWriter databaseWriter) {
    super(environment, passwordEncoder);
    this.databaseWriter = databaseWriter;
  }

  @Override
  public void run(String... args) {
    JpaDataGenerator jpaDataGenerator = createJpaDataGenerator();
    LOG.info("Beginning model data generation");
    Stopwatch stopwatch = new Stopwatch(true);
    jpaDataGenerator.generate();
    stopwatch.stop();
    LOG.info("Model data generation took {} seconds", stopwatch.getDurationSeconds());
    stopwatch.start();
    JpaToMsConverter converter = new JpaToMsConverter(jpaDataGenerator);
    converter.convert();
    stopwatch.stop();
    LOG.info(
        "Successfully converted model data to MicroStream data, took {} milliseconds",
        stopwatch.getDurationMillis());
    stopwatch.start();
    databaseWriter.writeAll(converter);
    databaseWriter.close();
    stopwatch.stop();
    LOG.info(
        "Successfully wrote model data to MicroStream storage, took {} seconds",
        stopwatch.getDurationSeconds());
  }
}
