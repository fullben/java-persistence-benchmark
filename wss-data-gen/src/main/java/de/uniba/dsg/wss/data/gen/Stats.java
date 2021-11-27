package de.uniba.dsg.wss.data.gen;

/**
 * Class for storing metadata regarding the creation and properties of a {@link DataModel}.
 *
 * @author Benedikt Full
 */
public class Stats {

  /** The total number of model objects that was generated. */
  private int totalModelObjectCount;
  /** The amount of milliseconds it took to generate the model objects. */
  private long durationMillis;
  /** A more human-readable representation of the {@link #durationMillis}. */
  private String duration;

  public Stats() {
    totalModelObjectCount = 0;
    durationMillis = 0;
    duration = null;
  }

  public int getTotalModelObjectCount() {
    return totalModelObjectCount;
  }

  public void setTotalModelObjectCount(int totalModelObjectCount) {
    this.totalModelObjectCount = totalModelObjectCount;
  }

  public long getDurationMillis() {
    return durationMillis;
  }

  public void setDurationMillis(long durationMillis) {
    this.durationMillis = durationMillis;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }
}
