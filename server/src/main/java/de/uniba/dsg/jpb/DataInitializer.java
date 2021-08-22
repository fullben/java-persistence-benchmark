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
    return new JpaDataGenerator(
        environment.getProperty("jpb.model.warehouse-count", Integer.class, 1),
        generateIds(),
        environment.getProperty("jpb.model.full-scale", Boolean.class, true),
        passwordEncoder);
  }

  abstract boolean generateIds();
}
