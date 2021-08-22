package de.uniba.dsg.jpb.util;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class RandomIdentifierGeneratorTests {

  @Test
  public void remainsCollisionFree() {
    int count = 1_000_000;
    Set<Long> values = new HashSet<>(count);
    RandomIdentifierGenerator generator = new RandomIdentifierGenerator();
    for (int i = 0; i < count; i++) {
      int size = values.size();
      values.add(generator.next());
      assertNotEquals(size, values.size());
    }
  }
}
