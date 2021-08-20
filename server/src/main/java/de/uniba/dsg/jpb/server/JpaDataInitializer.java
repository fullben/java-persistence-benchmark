package de.uniba.dsg.jpb.server;

import de.uniba.dsg.jpb.server.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.jpb.server.data.gen.jpa.JpaDatabaseWriter;
import de.uniba.dsg.jpb.server.util.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaDataInitializer extends DataInitializer {

  private static final Logger LOG = LogManager.getLogger(JpaDataInitializer.class);
  private final JpaDatabaseWriter databaseWriter;

  @Autowired
  public JpaDataInitializer(
      Environment environment, PasswordEncoder passwordEncoder, JpaDatabaseWriter databaseWriter) {
    super(environment, passwordEncoder);
    this.databaseWriter = databaseWriter;
  }

  @Override
  public void run(String... args) throws Exception {
    JpaDataGenerator jpaDataGenerator = createJpaDataGenerator();
    LOG.info("Beginning model data generation");
    Stopwatch stopwatch = new Stopwatch(true);
    jpaDataGenerator.generate();
    stopwatch.stop();
    LOG.info("Model data generation took {} seconds", stopwatch.getDurationSeconds());
    stopwatch.start();
    databaseWriter.writeAll(jpaDataGenerator);
    stopwatch.stop();
    LOG.info(
        "Successfully wrote model data to database, took {} seconds",
        stopwatch.getDurationSeconds());
  }

  @Override
  boolean generateIds() {
    return false;
  }
}
