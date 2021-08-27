package de.uniba.dsg.jpb.data.access.ms;

import de.uniba.dsg.jpb.data.model.ms.AddressData;
import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.OrderItemData;
import de.uniba.dsg.jpb.data.model.ms.PaymentData;
import de.uniba.dsg.jpb.data.model.ms.PersonData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.StockData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

public class DataManager implements AutoCloseable {

  private final transient ReentrantReadWriteLock lock;
  private transient EmbeddedStorageManager storageManager;
  private DataRoot root;
  private boolean closed;

  public DataManager(DataRoot root) {
    lock = new ReentrantReadWriteLock();
    this.root = root;
    closed = false;
  }

  public DataManager() {
    this(null);
  }

  public <T> T read(Function<DataRoot, T> operation) {
    verifyNotClosed();
    ReadLock readLock = lock.readLock();
    readLock.lock();
    verifyNotClosed();
    try {
      return validateReturnValue(operation.apply(getOrLoadRoot()));
    } finally {
      readLock.unlock();
    }
  }

  public void read(Consumer<DataRoot> operation) {
    verifyNotClosed();
    ReadLock readLock = lock.readLock();
    readLock.lock();
    verifyNotClosed();
    try {
      operation.accept(getOrLoadRoot());
    } finally {
      readLock.unlock();
    }
  }

  public <T> T write(BiFunction<DataRoot, EmbeddedStorageManager, T> operation) {
    verifyNotClosedAndInitialized();
    WriteLock writeLock = lock.writeLock();
    writeLock.lock();
    verifyNotClosedAndInitialized();
    try {
      return validateReturnValue(operation.apply(getOrLoadRoot(), storageManager));
    } finally {
      writeLock.unlock();
    }
  }

  public void write(BiConsumer<DataRoot, EmbeddedStorageManager> operation) {
    verifyNotClosedAndInitialized();
    WriteLock writeLock = lock.writeLock();
    writeLock.lock();
    verifyNotClosedAndInitialized();
    try {
      operation.accept(getOrLoadRoot(), storageManager);
    } finally {
      writeLock.unlock();
    }
  }

  public void setStorageManager(EmbeddedStorageManager storageManager) {
    this.storageManager = storageManager;
  }

  @Override
  public void close() {
    if (closed) {
      return;
    }
    if (storageManager != null) {
      storageManager.close();
    }
    root = null;
    closed = true;
  }

  private void verifyNotClosedAndInitialized() {
    verifyNotClosed();
    if (storageManager == null) {
      throw new IllegalStateException("Storage manager must be set");
    }
  }

  private void verifyNotClosed() {
    if (closed) {
      throw new IllegalStateException("Data manager has been closed");
    }
  }

  private DataRoot getOrLoadRoot() {
    if (root == null) {
      if (storageManager != null) {
        root = (DataRoot) storageManager.root();
      }
    }
    return root;
  }

  private static <T> T validateReturnValue(T t) {
    if (t == null) {
      return null;
    }
    if (t instanceof AddressData
        || t instanceof CarrierData
        || t instanceof DistrictData
        || t instanceof OrderData
        || t instanceof OrderItemData
        || t instanceof PaymentData
        || t instanceof PersonData
        || t instanceof ProductData
        || t instanceof StockData
        || t instanceof WarehouseData
        || t instanceof DataRoot
        || t instanceof EmbeddedStorageManager) {
      throw new IllegalArgumentException();
    }
    return t;
  }
}
