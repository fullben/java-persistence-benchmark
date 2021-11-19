package de.uniba.dsg.wss.data.gen.model;

import java.util.UUID;

public abstract class Base {

  private String id;

  public Base() {
    id = UUID.randomUUID().toString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
