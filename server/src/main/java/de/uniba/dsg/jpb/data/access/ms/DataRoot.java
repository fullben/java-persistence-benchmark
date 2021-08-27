package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import one.microstream.reference.Lazy;
import one.microstream.reference.Referencing;

/**
 * The root for the object graph which represents the MicroStream data model. Instances of this
 * class are used to persist and manage the data with MicroStream.
 *
 * @author Benedikt Full
 */
public class DataRoot {

  private final Map<String, ProductData> idToProduct;
  private final Map<String, CarrierData> idToCarrier;
  private final Map<String, Lazy<WarehouseData>> idToWarehouse;
  private final Map<String, EmployeeData> idToEmployee;

  public DataRoot() {
    idToProduct = new HashMap<>();
    idToCarrier = new HashMap<>();
    idToWarehouse = new HashMap<>();
    idToEmployee = new HashMap<>();
  }

  public void init(
      Collection<ProductData> products,
      Collection<CarrierData> carriers,
      Collection<WarehouseData> warehouses,
      Collection<EmployeeData> employees) {
    idToProduct.clear();
    idToProduct.putAll(
        products.stream().collect(Collectors.toMap(ProductData::getId, Function.identity())));
    idToCarrier.clear();
    idToCarrier.putAll(
        carriers.stream().collect(Collectors.toMap(CarrierData::getId, Function.identity())));
    idToWarehouse.clear();
    idToWarehouse.putAll(
        warehouses.stream().collect(Collectors.toMap(WarehouseData::getId, Lazy::Reference)));
    idToEmployee.clear();
    idToEmployee.putAll(
        employees.stream().collect(Collectors.toMap(EmployeeData::getId, Function.identity())));
  }

  public ProductData findProductById(String id) {
    return idToProduct.get(id);
  }

  public List<ProductData> findAllProducts() {
    return new ArrayList<>(idToProduct.values());
  }

  public CarrierData findCarrierById(String id) {
    return idToCarrier.get(id);
  }

  public List<CarrierData> findAllCarriers() {
    return new ArrayList<>(idToCarrier.values());
  }

  public WarehouseData findWarehouseById(String id) {
    return Lazy.get(idToWarehouse.get(id));
  }

  public List<WarehouseData> findAllWarehouses() {
    return idToWarehouse.values().stream().map(Referencing::get).collect(Collectors.toList());
  }

  public EmployeeData findEmployeeById(String id) {
    return idToEmployee.get(id);
  }

  public EmployeeData findEmployeeByUsername(String username) {
    return idToEmployee.values().stream()
        .filter(e -> e.getUsername().equals(username))
        .findAny()
        .orElse(null);
  }

  public List<EmployeeData> findAllEmployees() {
    return new ArrayList<>(idToEmployee.values());
  }
}
