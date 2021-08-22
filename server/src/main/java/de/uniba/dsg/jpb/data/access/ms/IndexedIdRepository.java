package de.uniba.dsg.jpb.data.access.ms;

import static java.util.Objects.requireNonNull;

import de.uniba.dsg.jpb.util.Identifiable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.text.html.Option;

public abstract class IndexedIdRepository<T extends Identifiable<I>, I>
    extends BaseRepository<T, I> {

  private final Map<I, T> idToItem;

  IndexedIdRepository() {
    super();
    idToItem = new HashMap<>();
  }

  @Override
  public T getById(I id) {
    requireNonNull(id);
    return read(() -> requireFound(idToItem.get(id)));
  }


  @Override
  public Optional<T> findById(I id) {
    requireNonNull(id);
    return read(() -> Optional.ofNullable(idToItem.get(id)));
  }



  @Override
  public List<T> findAll() {
    return read(() -> new ArrayList<>(idToItem.values()));
  }

  @Override
  public T save(T item) {
    requireNonNull(item);
    return write(
        () -> {
          if (item.getId() == null) {
            // New object, thus set id and add to all collections and persist
            item.setId(generateNextId(idToItem::containsKey));
            idToItem.put(item.getId(), item);
            getStorageManager().storeAll(idToItem);
          } else {
            if (!idToItem.containsKey(item.getId())) {
              // Illegal: an identifier was set by someone else
              throw new UnknownIdentifierException("Unable to find item with id " + item.getId());
            }
            // Existing object which is supposed to be overwritten
            getStorageManager().createEagerStorer().store(item);
          }
          return item;
        });
  }

  @Override
  public Collection<T> saveAll(Collection<T> items) {
    requireNonNull(items);
    if (items.isEmpty()) {
      return items;
    }
    return write(
        () -> {
          List<T> newItems = new ArrayList<>();
          List<T> updatedItems = new ArrayList<>();
          for (T item : items) {
            if (item.getId() == null) {
              // New object, thus set id and add to collection
              item.setId(generateNextId(idToItem::containsKey));
              idToItem.put(item.getId(), item);
              newItems.add(item);
            } else {
              // Existing object which is supposed to be overwritten
              if (!idToItem.containsKey(item.getId())) {
                // Illegal: an identifier was set by someone else
                throw new UnknownIdentifierException("Unable to find item for id " + item.getId());
              }
              updatedItems.add(item);
            }
          }
          if (!newItems.isEmpty()) {
            getStorageManager().store(idToItem);
          }
          if (!updatedItems.isEmpty()) {
            getStorageManager().createEagerStorer().store(updatedItems);
          }
          return new ArrayList<>(items);
        });
  }

  @Override
  public int count() {
    return read(idToItem::size);
  }

  final Map<I, T> getIndexedItems() {
    return idToItem;
  }
}
