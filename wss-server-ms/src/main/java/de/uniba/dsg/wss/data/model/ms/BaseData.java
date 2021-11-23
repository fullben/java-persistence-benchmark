package de.uniba.dsg.wss.data.model.ms;

import java.util.UUID;


public abstract class BaseData {

  protected final String id;

  public BaseData() {
    id = UUID.randomUUID().toString();
  }

  public BaseData(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
