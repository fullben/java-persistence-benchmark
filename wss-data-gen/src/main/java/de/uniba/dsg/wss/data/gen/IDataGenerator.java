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

  Stats generate();

  Configuration getConfiguration();
}
