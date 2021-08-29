package de.uniba.dsg.jpb.data.gen.jpa;

import de.uniba.dsg.jpb.data.gen.DataInitializer;
import de.uniba.dsg.jpb.util.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * If the server is launched in JPA persistence mode, this initializer generates a data model based
 * on the associated configuration properties and writes it to the configured JPA-based persistence
 * solution.
 *
 * @author Benedikt Full
 */
@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
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
  public void run(String... args) {
    JpaDataGenerator jpaDataGenerator = createJpaDataGenerator();
    LOG.info("Beginning model data generation");
    Stopwatch stopwatch = new Stopwatch(true);
    jpaDataGenerator.generate();
    stopwatch.stop();
    LOG.info("Model data generation took {}", stopwatch.getDuration());
    stopwatch.start();
    databaseWriter.writeAll(jpaDataGenerator);
    stopwatch.stop();
    LOG.info("Successfully wrote model data to database, took {}", stopwatch.getDuration());
  }
}
