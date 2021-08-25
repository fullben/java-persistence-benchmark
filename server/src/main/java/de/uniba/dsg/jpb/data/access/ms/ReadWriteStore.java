package de.uniba.dsg.jpb.data.access.ms;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.Supplier;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

public abstract class ReadWriteStore {

  private final transient ReentrantReadWriteLock lock;
  private transient EmbeddedStorageManager storageManager;

  ReadWriteStore() {
    lock = new ReentrantReadWriteLock();
    storageManager = null;
  }

  void setStorageManager(EmbeddedStorageManager storageManager) {
    this.storageManager = storageManager;
  }

  EmbeddedStorageManager getStorageManager() {
    return storageManager;
  }

  <T> T read(Supplier<T> operation) {
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

  <T> T write(Supplier<T> operation) {
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
}
