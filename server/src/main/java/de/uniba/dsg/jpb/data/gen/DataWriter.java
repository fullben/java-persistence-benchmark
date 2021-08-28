package de.uniba.dsg.jpb.data.gen;

/**
 * Implementers of this interface are capable of writing the objects provided by a {@link
 * DataProvider} to persistent storage.
 *
 * @param <W> the type used to represent warehouses
 * @param <E> the type used to represent employees
 * @param <P> the type used to represent products
 * @param <C> the type used to represent carriers
 * @author Benedikt Full
 */
public interface DataWriter<W, E, P, C> {

  void writeAll(DataProvider<W, E, P, C> dataProvider);
}
