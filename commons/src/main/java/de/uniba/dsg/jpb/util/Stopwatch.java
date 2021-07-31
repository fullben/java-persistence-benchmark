package de.uniba.dsg.jpb.util;

import java.util.concurrent.TimeUnit;

public class Stopwatch {

  private long start;
  private long stop;

  public Stopwatch() {
    this(false);
  }

  public Stopwatch(boolean start) {
    reset();
    if (start) {
      start();
    }
  }

  public void start() {
    start = System.nanoTime();
  }

  public long stop() {
    stop = System.nanoTime();
    return getDurationNanos();
  }

  public void reset() {
    start = 0;
    stop = 0;
  }

  public long getDurationNanos() {
    return stop - start;
  }

  public long getDurationMillis() {
    return TimeUnit.NANOSECONDS.toMillis(getDurationNanos());
  }

  public long getDurationSeconds() {
    return TimeUnit.NANOSECONDS.toSeconds(getDurationNanos());
  }
}
