package de.uniba.dsg.jpb.data.access.ms;

/**
 * Thrown to indicate that some MicroStream model object could not be found.
 *
 * @author Benedikt Full
 */
public class DataNotFoundException extends RuntimeException {

  public DataNotFoundException() {
    super();
  }

  public DataNotFoundException(String msg) {
    super(msg);
  }
}
