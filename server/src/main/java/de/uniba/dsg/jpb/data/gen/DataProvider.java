package de.uniba.dsg.jpb.data.gen;

import java.util.List;

public interface DataProvider<W, E, P, C> {

  List<W> getWarehouses();

  List<E> getEmployees();

  List<P> getProducts();

  List<C> getCarriers();
}
