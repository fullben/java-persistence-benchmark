package de.uniba.dsg.jpb.data.access.ms;

public class DataNotFoundException extends RuntimeException {

  public DataNotFoundException() {
    super();
  }

  public DataNotFoundException(String msg) {
    super(msg);
  }
}
