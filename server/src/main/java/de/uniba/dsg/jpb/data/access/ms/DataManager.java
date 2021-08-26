package de.uniba.dsg.jpb.data.access.ms;

import static java.util.Objects.requireNonNull;

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

public class DataManager {

  private final transient ReentrantReadWriteLock lock;
  private transient EmbeddedStorageManager storageManager;
  private final DataRoot root;

  public DataManager(DataRoot root) {
    lock = new ReentrantReadWriteLock();
    this.root = requireNonNull(root);
  }

  public <T> T read(Function<DataRoot, T> operation) {
    ReadLock readLock = lock.readLock();
    readLock.lock();
    try {
      return validateReturnValue(operation.apply(root));
    } finally {
      readLock.unlock();
    }
  }

  public void read(Consumer<DataRoot> operation) {
    ReadLock readLock = lock.readLock();
    readLock.lock();
    try {
      operation.accept(root);
    } finally {
      readLock.unlock();
    }
  }

  public <T> T write(BiFunction<DataRoot, EmbeddedStorageManager, T> operation) {
    verifyInitialized();
    WriteLock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      return validateReturnValue(operation.apply(root, storageManager));
    } finally {
      writeLock.unlock();
    }
  }

  public void write(BiConsumer<DataRoot, EmbeddedStorageManager> operation) {
    verifyInitialized();
    WriteLock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      operation.accept(root, storageManager);
    } finally {
      writeLock.unlock();
    }
  }

  private void verifyInitialized() {
    if (storageManager == null) {
      throw new IllegalStateException("Storage manager must be set");
    }
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

  public void setStorageManager(EmbeddedStorageManager storageManager) {
    this.storageManager = storageManager;
  }
}
