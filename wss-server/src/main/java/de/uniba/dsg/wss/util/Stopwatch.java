package de.uniba.dsg.wss.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * A very basic stopwatch implementation based on {@link System#nanoTime()}.
 *
 * @author Benedikt Full
 */
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

  public String getDuration() {
    Duration duration = Duration.ofNanos(getDurationNanos());
    int minutesPart = duration.toMinutesPart();
    if (minutesPart > 0) {
      int secondsPart = duration.toSecondsPart();
      String minString = minutesPart + " minutes";
      return secondsPart > 0 ? minString + " " + secondsPart + " seconds" : minString;
    } else {
      long secs = duration.toSecondsPart();
      if (secs > 0) {
        int millisPart = duration.toMillisPart();
        if (millisPart > 0) {
          return toSecondsAndMillisString(duration.toSeconds(), duration.toMillisPart()) + " seconds";
        }
        return duration.toSeconds() + " seconds";
      } else {
        long millis = duration.toMillis();
        if (millis > 0) {
          return millis + " ms";
        } else {
          return duration.toNanos() + " ns";
        }
      }
    }
  }

  private static String toSecondsAndMillisString(long seconds, long millis) {
    if (millis > 999) {
      throw new IllegalArgumentException("Millis value must be less than a full second");
    }
    if (millis < 10) {
      return seconds + ".00" + millis;
    } else if (millis < 100) {
      return seconds + ".0" + millis;
    } else {
      return seconds + "." + millis;
    }
  }
}
