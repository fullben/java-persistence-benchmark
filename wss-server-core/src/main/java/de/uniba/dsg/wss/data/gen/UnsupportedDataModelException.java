package de.uniba.dsg.wss.data.gen;

/**
 * Thrown whenever the {@link DataWriter#write(DataModel)} encounters an unsupported data model
 * type.
 *
 * @author Benedikt Full
 */
public class UnsupportedDataModelException extends RuntimeException {

  private static final long serialVersionUID = -3401738245721485920L;

  public UnsupportedDataModelException(String msg) {
    super(msg);
  }
}
