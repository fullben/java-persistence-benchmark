package de.uniba.dsg.wss.data.gen;

import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * Base implementation for pseudo-immutable data models.
 *
 * @param <P> the type representing a product
 * @param <W> the type representing a warehouse
 * @param <E> the type representing an employee
 * @param <C> the type representing a carrier
 * @author Benedikt Full
 */
public abstract class BaseDataModel<P, W, E, C> implements DataModel<P, W, E, C> {

  private final List<P> products;
  private final List<W> warehouses;
  private final List<E> employees;
  private final List<C> carriers;
  private final Stats stats;

  public BaseDataModel(
      List<P> products, List<W> warehouses, List<E> employees, List<C> carriers, Stats stats) {
    this.products = requireNonNull(products);
    this.warehouses = requireNonNull(warehouses);
    this.employees = requireNonNull(employees);
    this.carriers = requireNonNull(carriers);
    this.stats = requireNonNull(stats);
  }

  @Override
  public List<P> getProducts() {
    return products;
  }

  @Override
  public List<W> getWarehouses() {
    return warehouses;
  }

  @Override
  public List<E> getEmployees() {
    return employees;
  }

  @Override
  public List<C> getCarriers() {
    return carriers;
  }

  @Override
  public Stats getStats() {
    return stats;
  }
}
