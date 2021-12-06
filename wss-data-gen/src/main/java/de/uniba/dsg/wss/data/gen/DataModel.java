package de.uniba.dsg.wss.data.gen;

import java.util.List;

/**
 * A {@code DataModel} can be used to represent the data model of this benchmark in some
 * (persistence-solution-)specific structure.
 *
 * <p>This interface only defines accessors for the products, warehouses, employees, and carriers of
 * the data model. While the model has various other entities, these four were selected, as they are
 * the most top-level or independent entities. This means that for most implementations, having
 * direct access to these data types will suffice. All other types can usually be reached by
 * navigating the object graph accordingly.
 *
 * <p>Implementations of this interface must ensure access to the entire model structure. This means
 * that for example, if a certain part of the model is decoupled from the remainder and cannot be
 * reached, the implementation must provide additional methods for facilitating access to these
 * disjoint parts.
 *
 * @param <P> the type representing a product
 * @param <W> the type representing a warehouse
 * @param <E> the type representing an employee
 * @param <C> the type representing a carrier
 * @author Benedikt Full
 */
public interface DataModel<P, W, E, C> {

  /** @return the products of this model */
  List<P> getProducts();

  /** @return the warehouses of this model */
  List<W> getWarehouses();

  /** @return the employees of this model */
  List<E> getEmployees();

  /** @return the carriers of this model */
  List<C> getCarriers();

  /** @return information regarding the model represented by this object */
  Stats getStats();
}
