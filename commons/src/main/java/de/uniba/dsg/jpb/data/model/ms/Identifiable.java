package de.uniba.dsg.jpb.data.model.ms;

public interface Identifiable<T> {

  T getId();

  void setId(T id);
}
