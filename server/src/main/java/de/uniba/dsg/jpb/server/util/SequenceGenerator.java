package de.uniba.dsg.jpb.server.util;

public class SequenceGenerator {

  private Long last;

  public SequenceGenerator() {
    this(0);
  }

  public SequenceGenerator(long start) {
    last = start;
  }

  public synchronized long next() {
    return last++;
  }

  public synchronized long current() {
    return last;
  }
}
