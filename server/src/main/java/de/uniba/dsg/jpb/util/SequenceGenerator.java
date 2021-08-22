package de.uniba.dsg.jpb.util;

import java.util.concurrent.atomic.AtomicLong;

public class SequenceGenerator implements IdentifierGenerator<Long> {

  private final AtomicLong currentValue;

  public SequenceGenerator() {
    this(0);
  }

  public SequenceGenerator(long start) {
    currentValue = new AtomicLong(start);
  }

  public Long next() {
    return currentValue.addAndGet(1);
  }

  public Long current() {
    return currentValue.get();
  }
}
