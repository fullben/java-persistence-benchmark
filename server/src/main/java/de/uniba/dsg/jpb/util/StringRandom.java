package de.uniba.dsg.jpb.util;

import java.security.SecureRandom;
import java.util.Random;

public class StringRandom {

  private static final String DEFAULT_SYMBOLS = "abcdefghijklmnopqrstuvwxyz";
  private final Random random;
  private UniformRandom uniformRandom;
  private final char[] symbols;
  private final char[] buffer;

  public StringRandom(int min, int max) {
    if (min < 1 || max < 1 || max < min) {
      throw new IllegalArgumentException();
    }
    uniformRandom = new UniformRandom(min, max);
    random = new SecureRandom();
    this.symbols = DEFAULT_SYMBOLS.toCharArray();
    buffer = new char[max];
  }

  public StringRandom(int length, char[] symbols) {
    if (length < 1) {
      throw new IllegalArgumentException();
    }
    if (symbols == null || symbols.length < 1) {
      throw new IllegalArgumentException();
    }
    random = new SecureRandom();
    this.symbols = symbols;
    buffer = new char[length];
  }

  public StringRandom(int length) {
    this(length, DEFAULT_SYMBOLS.toCharArray());
  }

  public String nextString() {
    int max = uniformRandom == null ? buffer.length : uniformRandom.nextInt();
    for (int i = 0; i < max; i++) {
      buffer[i] = symbols[random.nextInt(symbols.length)];
    }
    return new String(buffer);
  }
}
