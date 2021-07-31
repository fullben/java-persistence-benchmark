package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.server.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Long> {}
