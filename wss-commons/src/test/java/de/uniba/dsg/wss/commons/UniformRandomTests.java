package de.uniba.dsg.wss.commons;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class UniformRandomTests {

  @ParameterizedTest
  @MethodSource("illegalIntArguments")
  public void intConstructorThrowsIllegalArgumentException(long min, long max) {
    assertThrows(IllegalArgumentException.class, () -> new UniformRandom(min, max));
  }

  @ParameterizedTest
  @MethodSource("illegalLongArguments")
  public void longConstructorThrowsIllegalArgumentException(double min, double max) {
    assertThrows(IllegalArgumentException.class, () -> new UniformRandom(min, max));
  }

  @Test
  public void nextIntRemainsInRange() {
    int min = 1;
    int max = 100;
    UniformRandom r = new UniformRandom(min, max);
    for (int i = 0; i < 1_000_000; i++) {
      int next = r.nextInt();
      assertTrue(next >= min && next <= max);
    }
  }

  @Test
  public void nextLongRemainsInRange() {
    int min = 1;
    int max = 100;
    UniformRandom r = new UniformRandom(min, max);
    for (int i = 0; i < 1_000_000; i++) {
      long next = r.nextLong();
      assertTrue(next >= min && next <= max);
    }
  }

  @Test
  public void nextDoubleRemainsInRange() {
    double min = 0.2;
    double max = 1.57;
    UniformRandom r = new UniformRandom(min, max);
    for (int i = 0; i < 1_000_000; i++) {
      double next = r.nextDouble();
      assertTrue(next >= min && next <= max);
    }
  }

  public static List<Arguments> illegalIntArguments() {
    return List.of(Arguments.of(2, 1), Arguments.of(1, 1));
  }

  public static List<Arguments> illegalLongArguments() {
    return List.of(Arguments.of(2.0, 1.0), Arguments.of(1.0, 1.0));
  }
}
