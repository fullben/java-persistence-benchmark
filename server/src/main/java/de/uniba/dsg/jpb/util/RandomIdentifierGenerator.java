package de.uniba.dsg.jpb.util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomIdentifierGenerator implements IdentifierGenerator<Long> {

  private final Random random;
  private volatile long current;

  public RandomIdentifierGenerator() {
    random = new SecureRandom();
    current = random.nextLong();
  }

  @Override
  public Long next() {
    current = random.nextLong();
    return current;
  }

  @Override
  public Long current() {
    return current;
  }
}
