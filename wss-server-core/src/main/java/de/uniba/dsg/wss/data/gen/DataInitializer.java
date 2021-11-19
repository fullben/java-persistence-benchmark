package de.uniba.dsg.wss.data.gen;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Implementations should generate an initial set of wholesale supplier model data and write this
 * data to persistent storage.
 *
 * @author Benedikt Full
 */
public abstract class DataInitializer implements CommandLineRunner {

  private final PasswordEncoder passwordEncoder;
  private final int modelWarehouseCount;
  private final boolean fullScaleModel;

  public DataInitializer(Environment environment, PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
    modelWarehouseCount = environment.getProperty("jpb.model.warehouse-count", Integer.class, 1);
    fullScaleModel = environment.getProperty("jpb.model.full-scale", Boolean.class, true);
  }

  protected int getModelWarehouseCount() {
    return modelWarehouseCount;
  }

  protected boolean isFullScaleModel() {
    return fullScaleModel;
  }

  protected PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }
}
