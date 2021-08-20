package de.uniba.dsg.jpb;

import de.uniba.dsg.jpb.data.gen.jpa.JpaDataGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class DataInitializer implements CommandLineRunner {

  private final Environment environment;
  private final PasswordEncoder passwordEncoder;

  DataInitializer(Environment environment, PasswordEncoder passwordEncoder) {
    this.environment = environment;
    this.passwordEncoder = passwordEncoder;
  }

  JpaDataGenerator createJpaDataGenerator() {
    final String warehouseCount = environment.getProperty("jpb.model.warehouse-count");
    final String fullScale = environment.getProperty("jpb.model.full-scale");
    return new JpaDataGenerator(
        warehouseCount == null ? 1 : Integer.parseInt(warehouseCount),
        generateIds(),
        fullScale == null || Boolean.parseBoolean(fullScale),
        passwordEncoder);
  }

  abstract boolean generateIds();
}
