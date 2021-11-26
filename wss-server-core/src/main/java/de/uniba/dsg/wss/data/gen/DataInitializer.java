package de.uniba.dsg.wss.data.gen;

import static org.apache.logging.log4j.util.Unbox.box;

import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Warehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

  private static final Logger LOG = LogManager.getLogger(DataInitializer.class);
  private final PasswordEncoder passwordEncoder;
  private final int modelWarehouseCount;
  private final boolean fullScaleModel;

  public DataInitializer(Environment environment, PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
    modelWarehouseCount = environment.getProperty("jpb.model.warehouse-count", Integer.class, 1);
    fullScaleModel = environment.getProperty("jpb.model.full-scale", Boolean.class, true);
  }

  /**
   * Returns a new {@link DefaultDataGenerator} based on the configuration properties read by this
   * instance.
   *
   * <p>Multiple calls to this method on the same bean will always return a new object, but the
   * configuration of the objects will remain the same.
   *
   * @return a new data generator
   * @see #generateData()
   */
  protected DefaultDataGenerator createDataGenerator() {
    return new DefaultDataGenerator(modelWarehouseCount, fullScaleModel, passwordEncoder::encode);
  }

  /**
   * Creates a new {@link DefaultDataGenerator} by using {@link #createDataGenerator()}, calls its
   * {@link DefaultDataGenerator#generate() generate()} method and returns the result of the method.
   *
   * <p>Note that as the generator itself does not perform any logging, this method uses the data
   * provided by the generator for some intermediate logging.
   *
   * @return a newly generated data model
   */
  protected DataModel<Product, Warehouse, Employee, Carrier> generateData() {
    DefaultDataGenerator generator = createDataGenerator();
    Configuration config = generator.getConfiguration();
    LOG.info(
        "Generating {} products, {} warehouses, {} districts, {} employees, {} customers, and {} orders",
        box(config.getProductCount()),
        box(config.getWarehouseCount()),
        box(config.getDistrictCount()),
        box(config.getEmployeeCount()),
        box(config.getCustomerCount()),
        box(config.getOrderCount()));
    DataModel<Product, Warehouse, Employee, Carrier> model = generator.generate();
    LOG.info(
        "Generated {} model data objects, took {}",
        box(model.getStats().getTotalModelObjectCount()),
        model.getStats().getDuration());
    return model;
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
   * Implementations of this method must use the {@link DefaultDataGenerator} provided by {@link
   * #createDataGenerator()} or {@link #generateData()} in conjunction with the appropriate {@link
   * DataConverter} implementation to generate the type of model data required by the backing
   * persistence solution. The converted data must be written to persistent storage using a {@link
   * DataWriter} implementation.
   *
   * @throws Exception on error
   */
  public abstract void initializePersistentData() throws Exception;
}
