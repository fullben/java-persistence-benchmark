package de.uniba.dsg.jpb.server.util;

import java.security.SecureRandom;
import java.util.Random;

public class NonUniformRandom {

  private static final Random RANDOM = new SecureRandom();
  private final int min;
  private final int max;
  private final int a;
  private final int c;

  public NonUniformRandom(int min, int max, int a) {
    validate(min, max, a);
    this.min = min;
    this.max = max;
    this.a = a;
    c = random(0, a);
  }

  public NonUniformRandom(int min, int max, int a, int c) {
    validate(min, max, a, c);
    this.min = min;
    this.max = max;
    this.a = a;
    this.c = c;
  }

  public int nextInt() {
    return (((random(0, a) | random(min, max)) + c) % (max - min + 1)) + min;
  }

  public long nextLong() {
    return nextInt();
  }

  private static int random(int min, int max) {
    return RANDOM.nextInt(max + 1 - min) + min;
  }

  private static void validateRange(int min, int max) {
    if (min > max || min == max) {
      throw new IllegalArgumentException("Min must be smaller than max");
    }
    if (min < 0) {
      throw new IllegalArgumentException("Min must be greater or equal to zero");
    }
  }

  private static void validate(int min, int max, int a) {
    validateRange(min, max);
    if (a < min || a > max) {
      throw new IllegalArgumentException("The param a must be in range defined by min and max");
    }
  }

  private static void validate(int min, int max, int a, int c) {
    validate(min, max, a);
    if (c < min || c > a) {
      throw new IllegalArgumentException("The param c must be in range defined by min and a");
    }
  }
}
