package de.uniba.dsg.jpb.data.gen;

import java.util.List;

/**
 * Implementers of this interface are capable of providing access to a fully valid and consistent
 * wholesale supplier data model.
 *
 * @param <W> the type used to represent warehouses
 * @param <E> the type used to represent employees
 * @param <P> the type used to represent products
 * @param <C> the type used to represent carriers
 * @see DataWriter
 * @author Benedikt Full
 */
public interface DataProvider<W, E, P, C> {

  List<W> getWarehouses();

  List<E> getEmployees();

  List<P> getProducts();

  List<C> getCarriers();
}
