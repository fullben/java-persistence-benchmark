package de.uniba.dsg.wss.data.gen;

import de.uniba.dsg.wss.data.gen.model.Carrier;
import de.uniba.dsg.wss.data.gen.model.Employee;
import de.uniba.dsg.wss.data.gen.model.Product;
import de.uniba.dsg.wss.data.gen.model.Warehouse;
import java.util.List;

/**
 * Class for storing the converted model data produced by {@link DataGenerator} implementations.
 *
 * @author Benedikt Full
 */
public class DataGeneratorModel extends BaseDataModel<Product, Warehouse, Employee, Carrier> {

  public DataGeneratorModel(
      List<Product> products,
      List<Warehouse> warehouses,
      List<Employee> employees,
      List<Carrier> carriers,
      Stats stats) {
    super(products, warehouses, employees, carriers, stats);
  }
}
