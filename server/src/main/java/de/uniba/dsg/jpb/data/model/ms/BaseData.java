package de.uniba.dsg.jpb.data.model.ms;

import java.util.UUID;

/**
 * The base class for all MicroStream data classes. It defines the identifier for the object, which
 * is a UUID.
 *
 * @author Benedikt Full
 */
public abstract class BaseData {

  private String id;

  public BaseData() {
    id = UUID.randomUUID().toString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
