package de.uniba.dsg.jpb.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Can be used to select a random item from a list.
 *
 * @author Benedikt Full
 */
public class RandomSelector<T> {

  private final List<T> list;
  private final int size;
  private final Random random;

  /**
   * Creates a new instance based on a shallow copy of the provided list. This means that
   * modifications to the given list after having called this constructor will not affect the
   * element selection performed by this selector.
   *
   * @param list a non-{@code null}, non-empty list
   */
  public RandomSelector(List<T> list) {
    if (list == null || list.isEmpty()) {
      throw new IllegalArgumentException("List must not be neither null nor empty");
    }
    this.list = new ArrayList<>(list);
    size = list.size();
    random = new SecureRandom();
  }

  /**
   * Selects a random element from the collection and returns it.
   *
   * @return the randomly selected element, may be {@code null} if the list contains {@code null}
   *     values
   */
  public T next() {
    return list.get(random.nextInt(size));
  }
}
