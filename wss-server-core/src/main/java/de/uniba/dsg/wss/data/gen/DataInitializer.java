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

  /**
   * Returns a new {@link DataGenerator} based on the configuration properties read by this
   * instance.
   *
   * <p>Multiple calls to this method on the same bean will always return a new object, but the
   * configuration of the objects will remain the same.
   *
   * @return a new data generator
   */
  protected DataGenerator createDataGenerator() {
    return new DataGenerator(modelWarehouseCount, fullScaleModel, passwordEncoder);
  }

  /**
   * Callback used to run the bean. Initializes the initial persistent data state using the
   * implementation of {@link #initializePersistentData()}.
   *
   * @param args are ignored
   * @throws Exception on error
   */
  @Override
  public void run(String... args) throws Exception {
    initializePersistentData();
  }

  /**
   * Implementations of this method must use the {@link DataGenerator} provided by {@link
   * #createDataGenerator()} in conjunction with the appropriate {@link DataConverter}
   * implementation to generate the type of model data required by the backing persistence solution.
   * The converted data must be written to persistent storage using a {@link DataWriter}
   * implementation.
   *
   * @throws Exception on error
   */
  public abstract void initializePersistentData() throws Exception;
}
