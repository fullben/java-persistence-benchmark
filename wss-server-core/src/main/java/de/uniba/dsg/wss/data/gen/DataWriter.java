package de.uniba.dsg.wss.data.gen;

/**
 * Implementers of this interface are capable of writing the objects provided by a data provider to
 * persistent storage.
 *
 * @param <P> the type representing a product
 * @param <W> the type representing a warehouse
 * @param <E> the type representing an employee
 * @param <C> the type representing a carrier
 * @see DataConverter
 * @author Benedikt Full
 */
public interface DataWriter<P, W, E, C> {

  /**
   * Writes the given model data to the backing persistence solution.
   *
   * @param model the non-{@code null} data model
   */
  void write(DataModel<P, W, E, C> model);
}
