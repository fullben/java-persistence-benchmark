package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.model.Item;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

  @Query("select id from Item")
  List<Long> findAllItemIds();
}
