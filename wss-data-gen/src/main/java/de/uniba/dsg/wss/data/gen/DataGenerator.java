package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Warehouse;

/**
 * A {@code DataGenerator} can be used for creating a persistence-solution independent data model.
 *
 * @see DataModel
 * @author Benedikt Full
 */
public interface DataGenerator {

  /**
   * Generates a data model based on the configuration of the generator instance.
   *
   * <p>The generator will keep no internal reference to the generated and returned data.
   *
   * @return the generated data model
   */
  DataModel<Product, Warehouse, Employee, Carrier> generate();
}
