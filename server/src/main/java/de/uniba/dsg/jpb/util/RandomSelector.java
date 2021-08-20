package de.uniba.dsg.jpb.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSelector<T> {

  private final List<T> list;
  private final int size;
  private final Random random;

  public RandomSelector(List<T> list) {
    this.list = new ArrayList<>(list);
    size = list.size();
    random = new SecureRandom();
  }

  public T next() {
    return list.get(random.nextInt(size));
  }
}
