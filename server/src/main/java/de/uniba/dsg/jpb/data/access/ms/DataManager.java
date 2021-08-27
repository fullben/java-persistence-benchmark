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

/**
 * The {@code DataManager} serves as both access provider to and guard of the MicroStream data
 * model. The guard functionality is necessary due to the fact that MicroStream persistence works
 * directly with a Java object graph. As in case of this application, this data graph is highly
 * mutable, great care must be taken when accessing it. Unregulated access to the data held by a
 * MicroStream-managed instance of {@link DataRoot} may quickly lead to consistency issues, as
 * MicroStream has no generic facilities for providing a thread-safe, transaction-protected data
 * access.
 *
 * <p>This manager facilitates this by the means of providing methods for read-only and modifying
 * access to the MicroStream-managed data, internally protected by a {@link ReentrantReadWriteLock}.
 *
 * <p>At the same time it must be noted that the manager can only make consistency guarantees as
 * long as users of the manager honor its implicit contract:
 *
 * <ul>
 *   <li>Don't pass model objects out of the methods of the manager; neither as return value nor by
 *       assigning them to outside variables.
 *   <li>Don't make any changes to model objects while using the read-only methods of the manager.
 * </ul>
 *
 * Furthermore, the manager provides no automatic error handling. Rollbacks must be implemented by
 * the caller. Runtime exceptions that occur in any of the methods of the manager have the potential
 * to leave the object graph in an undesirable state. Callers should thus only perform operations on
 * the object graph once they have performed all necessary validation to reduce the likelihood of
 * unexpected exceptions.
 *
 * @author Benedikt Full
 */
public class DataManager implements AutoCloseable {

  private final transient ReentrantReadWriteLock lock;
  private transient EmbeddedStorageManager storageManager;
  private transient boolean closed;
  private DataRoot root;

  public DataManager() {
    this(null);
  }

  public DataManager(EmbeddedStorageManager storageManager) {
    lock = new ReentrantReadWriteLock();
    this.storageManager = storageManager;
    closed = false;
    this.root = null;
  }

  /**
   * Provides read access to the data maintained by this manager. Read operations must never modify
   * any of the objects found in the provided {@code DataRoot} instance.
   *
   * @param operation the read operation
   * @param <T> the type of value returned by the operation
   * @return a read or computed value, must not be an instance of any of the data model classes
   * @see #read(Consumer)
   */
  public <T> T read(Function<DataRoot, T> operation) {
    verifyNotClosed();
    ReadLock readLock = lock.readLock();
    readLock.lock();
    try {
      verifyNotClosed();
      return validateReturnValue(operation.apply(getOrLoadRoot()));
    } finally {
      readLock.unlock();
    }
  }

  /**
   * Provides read access to the data maintained by this manager. Read operations must never modify
   * any of the objects found in the provided {@code DataRoot} instance.
   *
   * @param operation the read operation
   * @see #read(Function)
   */
  public void read(Consumer<DataRoot> operation) {
    verifyNotClosed();
    ReadLock readLock = lock.readLock();
    readLock.lock();
    try {
      verifyNotClosed();
      operation.accept(getOrLoadRoot());
    } finally {
      readLock.unlock();
    }
  }

  /**
   * Provides write access to the data maintained by this manager. If the given operation makes
   * changes to the data model, it should use the given storage manager to persist these changes, as
   * the manager has no means of automatically detecting or persisting these modifications.
   *
   * @param operation the write operation
   * @param <T> the type of value returned by the operation
   * @return a read or computed value, must not be an instance of any of the data model classes
   * @see #write(BiConsumer)
   */
  public <T> T write(BiFunction<DataRoot, EmbeddedStorageManager, T> operation) {
    verifyNotClosedAndInitialized();
    WriteLock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      verifyNotClosedAndInitialized();
      return validateReturnValue(operation.apply(getOrLoadRoot(), storageManager));
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * Provides write access to the data maintained by this manager. If the given operation makes
   * changes to the data model, it should use the given storage manager to persist these changes, as
   * the manager has no means of automatically detecting or persisting these modifications.
   *
   * @param operation the write operation
   * @see #write(BiFunction)
   */
  public void write(BiConsumer<DataRoot, EmbeddedStorageManager> operation) {
    verifyNotClosedAndInitialized();
    WriteLock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      verifyNotClosedAndInitialized();
      operation.accept(getOrLoadRoot(), storageManager);
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * Sets the given storage manager and its root as the data managed by this instance. If this
   * manager already has a storage manager and managed root object, these objects are discarded and
   * replaced by the given instance.
   *
   * @param storageManager the new storage manager of the instance, may be {@code null}
   */
  public void setStorageManager(EmbeddedStorageManager storageManager) {
    this.storageManager = storageManager;
    root = null;
  }

  /**
   * Closes the MicroStream storage manager of this instance (if it has one) and removes the
   * reference to the root object.
   */
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
