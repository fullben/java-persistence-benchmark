package de.uniba.dsg.wss.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Generator for creating random numbers in specific ranges and with specific precision.
 *
 * @author Benedikt Full
 */
public class UniformRandom {

  private final double min;
  private final double max;
  private final int precision;
  private final Random random;

  public UniformRandom(int min, int max) {
    this(min, max, 0, false);
  }

  public UniformRandom(double min, double max) {
    this(min, max, 0, false);
  }

  public UniformRandom(double min, double max, int precision) {
    this(min, max, precision, true);
  }

  private UniformRandom(double min, double max, int precision, boolean validatePrecision) {
    if (min >= max) {
      throw new IllegalArgumentException();
    }
    if (validatePrecision && precision < 1) {
      throw new IllegalArgumentException();
    }
    this.min = min;
    this.max = max;
    this.precision = precision;
    random = new SecureRandom();
  }

  public int nextInt() {
    return (int) (random.nextInt((int) max + 1 - (int) min) + min);
  }

  public long nextLong() {
    return nextInt();
  }

  public double nextDouble() {
    return precision > 0 ? round(nextRandomDouble(), 2) : nextRandomDouble();
  }

  private double nextRandomDouble() {
    return min + (max - min) * random.nextDouble();
  }

  private static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }
    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
}
