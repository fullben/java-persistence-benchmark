package de.uniba.dsg.jpb.util;

public interface IdentifierGenerator<T> {

  T next();

  T current();
}
