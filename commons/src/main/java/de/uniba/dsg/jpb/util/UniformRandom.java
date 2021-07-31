package de.uniba.dsg.jpb.util;

import java.security.SecureRandom;
import java.util.Random;

public class UniformRandom {

  private final double min;
  private final double max;
  private final Random random;

  public UniformRandom(int min, int max) {
    if (min >= max) {
      throw new IllegalArgumentException();
    }
    this.min = min;
    this.max = max;
    random = new SecureRandom();
  }

  public UniformRandom(double min, double max) {
    if (min >= max) {
      throw new IllegalArgumentException();
    }
    this.min = min;
    this.max = max;
    random = new SecureRandom();
  }

  public int nextInt() {
    return (int) (random.nextInt((int) max + 1 - (int) min) + min);
  }

  public long nextLong() {
    return nextInt();
  }

  public double nextDouble() {
    return min + (max - min) * random.nextDouble();
  }
}
