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

public class DataRoot {

  private final Map<Long, ProductData> idToProduct;
  private final Map<Long, CarrierData> idToCarrier;
  private final Map<Long, Lazy<WarehouseData>> idToWarehouse;
  private final Map<Long, EmployeeData> idToEmployee;

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

  public ProductData findProductById(Long id) {
    ProductData product = idToProduct.get(id);
    if (product == null) {
      throw new DataNotFoundException();
    }
    return product;
  }

  public List<ProductData> findAllProducts() {
    return new ArrayList<>(idToProduct.values());
  }

  public CarrierData findCarrierById(Long id) {
    CarrierData carrier = idToCarrier.get(id);
    if (carrier == null) {
      throw new DataNotFoundException();
    }
    return carrier;
  }

  public List<CarrierData> findAllCarriers() {
    return new ArrayList<>(idToCarrier.values());
  }

  public WarehouseData findWarehouseById(Long id) {
    Lazy<WarehouseData> lazy = idToWarehouse.get(id);
    if (lazy == null) {
      throw new DataNotFoundException();
    }
    return lazy.get();
  }

  public List<WarehouseData> findAllWarehouses() {
    return idToWarehouse.values().stream().map(Referencing::get).collect(Collectors.toList());
  }

  public EmployeeData findEmployeeById(Long id) {
    EmployeeData employee = idToEmployee.get(id);
    if (employee == null) {
      throw new DataNotFoundException();
    }
    return employee;
  }

  public EmployeeData findEmployeeByUsername(String username) {
    return idToEmployee.values().stream()
        .filter(e -> e.getUsername().equals(username))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public List<EmployeeData> findAllEmployees() {
    return new ArrayList<>(idToEmployee.values());
  }
}
