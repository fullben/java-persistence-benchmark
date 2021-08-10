package de.uniba.dsg.jpb.server.data.access.ms;

import de.uniba.dsg.jpb.data.model.Identifiable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

public abstract class IndexedIdRepository<T extends Identifiable<I>, I> {

  private transient EmbeddedStorageManager storageManager;
  private final Map<I, T> idToItem;

  IndexedIdRepository(EmbeddedStorageManager storageManager) {
    this.storageManager = storageManager;
    idToItem = new HashMap<>();
  }

  IndexedIdRepository() {
    storageManager = null;
    idToItem = new HashMap<>();
  }

  public T findById(I id) {
    return idToItem.get(id);
  }

  public List<T> findAll() {
    return new ArrayList<>(idToItem.values());
  }

  public T save(T item) {
    T res = idToItem.put(item.getId(), item);
    persist();
    return res;
  }

  public List<T> saveAll(Collection<T> items) {
    for (T item : items) {
      idToItem.put(item.getId(), item);
    }
    persist();
    return new ArrayList<>(items);
  }

  public void delete(T item) {
    idToItem.remove(item.getId());
    persist();
  }

  public void deleteAll(Collection<T> items) {
    for (T item : items) {
      idToItem.remove(item.getId());
    }
    persist();
  }

  private void persist() {
    storageManager.storeAll(idToItem);
  }

  void setStorageManager(EmbeddedStorageManager storageManager) {
    this.storageManager = storageManager;
  }

  EmbeddedStorageManager getStorageManager() {
    return storageManager;
  }
}
