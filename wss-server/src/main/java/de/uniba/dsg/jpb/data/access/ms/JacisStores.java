package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.data.model.ms.BaseData;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jacis.store.JacisStore;

/**
 * Utility class for working with {@link JacisStore JacisStores}.
 *
 * @author Benedikt Full
 */
public final class JacisStores {

  private JacisStores() {
    throw new AssertionError();
  }

  /**
   * Returns a stream of all objects from the given store matching the provided filter. The stream
   * returned by this method should be the same as if one were to call {@link
   * JacisStore#stream(Predicate)}. The advantage of using this method is that the performance of it
   * may be better for stores with many objects than that of the regular {@code stream(Predicate)}
   * method. Note that apart from store size, the performance of this method is also influenced by
   * the number of available CPU cores.
   *
   * <p>As parallel streams cannot be used for retrieving writable objects from a store reliably (as
   * this may result in one of the stream executing threads becoming the authorized writer thread),
   * this method first runs a parallel, read-only stream and gets the ids of all objects that match
   * the filter. The writable objects are then retrieved from the store using the {@link
   * JacisStore#get(Object)} method and returned as stream.
   *
   * @param store the store from which to stream writable objects
   * @param filter the criteria which the objects of the returned stream must match
   * @param <T> the type of data held by the store
   * @return a stream containing all items of the store that matched the given filter
   */
  public static <T extends BaseData> Stream<T> fastStream(
      JacisStore<String, T> store, Predicate<T> filter) {
    List<String> ids =
        store.streamReadOnly(filter).parallel().map(BaseData::getId).collect(Collectors.toList());
    return ids.stream().map(store::get);
  }
}
