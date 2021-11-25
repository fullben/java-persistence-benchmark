package de.uniba.dsg.wss.commons;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * A very basic stopwatch implementation based on {@link System#nanoTime()}.
 *
 * <p>Stopwatches are primarily meant for measuring the execution time of tasks. They also provide
 * the facility for returning the measured time in a loggable format, see {@link #getDuration()}.
 *
 * <p>The following snippet illustrates a typical use case for the stopwatch:
 *
 * <pre>
 *   Stopwatch stopwatch = new Stopwatch();
 *   stopwatch.start();
 *   // Do some resource/time-intensive task here
 *   ...
 *   stopwatch.stop();
 *   LOG.info("Task took {}", stopwatch.getDuration());
 * </pre>
 *
 * @author Benedikt Full
 */
public class Stopwatch {

  private long start;
  private long stop;
  private boolean stopped;

  public Stopwatch() {
    this(false);
  }

  public Stopwatch(boolean start) {
    reset();
    if (start) {
      start();
    }
  }

  /**
   * Starts or restarts time measurement.
   *
   * @see #stop()
   */
  public void start() {
    start = System.nanoTime();
    stop = 0;
    stopped = false;
  }

  /**
   * Stops the time measuring and returns the elapsed time in nanoseconds.
   *
   * <p>Elapsed time is the amount of time that has passed since stopwatch initialization or the
   * most recent call to {@link #start()}.
   *
   * @return the elapsed duration in nanoseconds
   */
  public long stop() {
    if (stopped) {
      return getDurationNanos();
    }
    stop = System.nanoTime();
    stopped = true;
    return getDurationNanos();
  }

  /** Resets the internal state of the stopwatch to stopped. */
  private void reset() {
    start = 0;
    stop = 0;
    stopped = true;
  }

  /**
   * Returns the amount of nanoseconds that have been measured during the most recent measurement
   * interval.
   *
   * @return the elapsed duration in nanoseconds
   * @see #getDurationMillis()
   * @see #getDurationSeconds()
   * @see #getDuration()
   */
  public long getDurationNanos() {
    requireStopped();
    return stop - start;
  }

  /**
   * Returns the amount of milliseconds that have been measured during the most recent measurement
   * interval.
   *
   * @return the elapsed duration in milliseconds
   * @see #getDurationNanos()
   * @see #getDurationSeconds()
   * @see #getDuration()
   */
  public long getDurationMillis() {
    requireStopped();
    return TimeUnit.NANOSECONDS.toMillis(getDurationNanos());
  }

  /**
   * Returns the amount of seconds that have been measured during the most recent measurement
   * interval.
   *
   * @return the elapsed duration in seconds
   * @see #getDurationNanos()
   * @see #getDurationMillis()
   * @see #getDuration()
   */
  public long getDurationSeconds() {
    requireStopped();
    return TimeUnit.NANOSECONDS.toSeconds(getDurationNanos());
  }

  /**
   * Returns the amount of time that has measured during the most recent measurement interval in a
   * human-readable format. Examples:
   *
   * <ul>
   *   <li>68 nanoseconds: {@code "68 ns"}
   *   <li>23 milliseconds: {@code "23 ms"}
   *   <li>1032 milliseconds: {@code "1.032 seconds"}
   *   <li>1840 seconds: {@code "30.666 minutes"}
   * </ul>
   *
   * <p>Note that the current implementation is only meant for returning human-readable
   * representations of the elapsed time in nanoseconds, milliseconds, seconds, and minutes. Hours,
   * days, or even larger units of time are not used.
   *
   * @return the elapsed duration as a human-readable string
   * @see #getDurationNanos()
   * @see #getDurationMillis()
   * @see #getDurationSeconds()
   */
  public String getDuration() {
    requireStopped();
    Duration duration = Duration.ofNanos(getDurationNanos());
    if (duration.toMinutesPart() > 0) {
      return toMinutesAndFractionString(duration);
    } else {
      long secs = duration.toSecondsPart();
      if (secs > 0) {
        int millisPart = duration.toMillisPart();
        if (millisPart > 0) {
          return toSecondsAndMillisString(duration.toSeconds(), duration.toMillisPart())
              + " seconds";
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

  private static String toMinutesAndFractionString(Duration duration) {
    if (duration.toSecondsPart() == 0) {
      return String.valueOf(duration.toMinutesPart());
    }
    // Convert to minute fraction
    String min = String.valueOf((float) duration.toSecondsPart() / 60);
    if (min.startsWith("0.")) {
      // Remove first two symbols
      min = min.substring(2);
    }
    // Ensure there are no more than 3 digits (NO rounding, just cut off)
    if (min.length() > 3) {
      min = min.substring(0, 3);
    }
    return duration.toMinutesPart() + "." + min;
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

  private void requireStopped() {
    if (!stopped) {
      throw new IllegalStateException("Stopwatch is currently running");
    }
  }
}
