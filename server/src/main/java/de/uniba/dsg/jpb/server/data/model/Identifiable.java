package de.uniba.dsg.jpb.server.data.model;

public interface Identifiable<T> {

  T getId();

  void setId(T id);
}
