package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.util.Identifiable;
import java.util.Collection;
import java.util.Optional;

public interface Store<T extends Identifiable<I>, I> {

  T getById(I id);

  Optional<T> findById(I id);

  Collection<T> findAll();

  T save(T item);

  Collection<T> saveAll(Collection<T> item);

  void clear();

  int count();
}
