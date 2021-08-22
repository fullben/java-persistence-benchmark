package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.util.Identifiable;
import de.uniba.dsg.jpb.util.IdentifierGenerator;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.Predicate;
import java.util.function.Supplier;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

public abstract class BaseRepository<T extends Identifiable<I>, I> {

  private final transient ReentrantReadWriteLock lock;
  private transient EmbeddedStorageManager storageManager;
  private transient IdentifierGenerator<I> idGenerator;

  BaseRepository() {
    lock = new ReentrantReadWriteLock();
    storageManager = null;
    idGenerator = null;
  }

  public abstract T getById(I id);

  public abstract Optional<T> findById(I id);

  public abstract List<T> findAll();

  public abstract T save(T item);

  public abstract Collection<T> saveAll(Collection<T> item);

  public abstract void clear();

  public abstract int count();

  void setStorageManager(EmbeddedStorageManager storageManager) {
    this.storageManager = storageManager;
  }

  EmbeddedStorageManager getStorageManager() {
    return storageManager;
  }

  void setIdGenerator(IdentifierGenerator<I> idGenerator) {
    this.idGenerator = idGenerator;
  }

  I generateNextId(Predicate<I> notAllowed) {
    I nextId;
    do {
      nextId = idGenerator.next();
    } while (notAllowed.test(nextId));
    return nextId;
  }

  <S> S read(Supplier<S> operation) {
    ReadLock readLock = lock.readLock();
    readLock.lock();
    try {
      return operation.get();
    } finally {
      readLock.unlock();
    }
  }

  void read(Runnable operation) {
    ReadLock readLock = lock.readLock();
    readLock.lock();
    try {
      operation.run();
    } finally {
      readLock.unlock();
    }
  }

  <S> S write(Supplier<S> operation) {
    WriteLock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      return operation.get();
    } finally {
      writeLock.unlock();
    }
  }

  void write(Runnable operation) {
    WriteLock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      operation.run();
    } finally {
      writeLock.unlock();
    }
  }

  static <T> T requireFound(T value) {
    if (value == null) {
      throw new DataNotFoundException();
    }
    return value;
  }
}
