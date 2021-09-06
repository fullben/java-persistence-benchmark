package de.uniba.dsg.jpb.data.gen;

/**
 * Implementers of this interface are capable of writing the objects provided by a data provider to
 * persistent storage.
 *
 * @param <P> the type of the data provider
 * @author Benedikt Full
 */
public interface DataWriter<P> {

  void writeAll(P provider);
}
