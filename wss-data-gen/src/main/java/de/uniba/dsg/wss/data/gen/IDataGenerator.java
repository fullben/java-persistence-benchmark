package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Warehouse;
import java.util.List;

public interface IDataGenerator {

  List<Warehouse> getWarehouses();

  List<Employee> getEmployees();

  List<Product> getProducts();

  List<Carrier> getCarriers();

  boolean isDataGenerated();

  /**
   * Generates a data model based on the configuration (of which a summary can be acquired by
   * calling {@link #getConfiguration()}) of this instance.
   *
   * @return information regarding the data generation execution, will never be {@code null}
   */
  Stats generate();

  Configuration getConfiguration();
}
