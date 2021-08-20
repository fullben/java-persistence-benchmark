package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.data.model.Identifiable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

public abstract class IndexedIdRepository<T extends Identifiable<I>, I> {

  private final Map<I, T> idToItem;
  private final transient ReentrantReadWriteLock lock;
  private transient EmbeddedStorageManager storageManager;

  IndexedIdRepository(EmbeddedStorageManager storageManager) {
    this.storageManager = storageManager;
    lock = new ReentrantReadWriteLock();
    idToItem = new HashMap<>();
  }

  IndexedIdRepository() {
    idToItem = new HashMap<>();
    lock = new ReentrantReadWriteLock();
    storageManager = null;
  }

  public T findById(I id) {
    return read(() -> idToItem.get(id));
  }

  public List<T> findAll() {
    return read(() -> new ArrayList<>(idToItem.values()));
  }

  public T save(T item) {
    return write(
        () -> {
          T res = idToItem.put(item.getId(), item);
          persist();
          return res;
        });
  }

  public List<T> saveAll(Collection<T> items) {
    return write(
        () -> {
          for (T item : items) {
            idToItem.put(item.getId(), item);
          }
          persist();
          return new ArrayList<>(items);
        });
  }

  public void delete(T item) {
    write(
        () -> {
          idToItem.remove(item.getId());
          persist();
        });
  }

  public void deleteAll(Collection<T> items) {
    write(
        () -> {
          for (T item : items) {
            idToItem.remove(item.getId());
          }
          persist();
        });
  }

  void setStorageManager(EmbeddedStorageManager storageManager) {
    this.storageManager = storageManager;
  }

  EmbeddedStorageManager getStorageManager() {
    return storageManager;
  }

  private void persist() {
    storageManager.storeAll(idToItem);
  }

  private <S> S read(Supplier<S> operation) {
    lock.readLock().lock();
    try {
      return operation.get();
    } finally {
      lock.readLock().unlock();
    }
  }

  private void read(Runnable operation) {
    lock.readLock().lock();
    try {
      operation.run();
    } finally {
      lock.readLock().unlock();
    }
  }

  private <S> S write(Supplier<S> operation) {
    lock.writeLock().lock();
    try {
      return operation.get();
    } finally {
      lock.writeLock().unlock();
    }
  }

  private void write(Runnable operation) {
    lock.writeLock().lock();
    try {
      operation.run();
    } finally {
      lock.writeLock().unlock();
    }
  }
}
