package de.uniba.dsg.jpb.data.model.ms;

import java.util.UUID;

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
