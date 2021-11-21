package de.uniba.dsg.wss.data.gen;

/**
 * Implementers of this interface are capable of writing the objects provided by a data provider to
 * persistent storage.
 *
 * @param <P> the type of the data provider
 * @see DataConverter
 * @author Benedikt Full
 */
public interface DataWriter<P extends DataConverter> {

  /**
   * Writes the model data maintained by the given provider to the backing persistence solution.
   *
   * <p>Implementations of this method may assume that the given provider already holds valid model
   * data.
   *
   * @param provider the non-{@code null} data provider
   */
  void writeAll(P provider);
}
