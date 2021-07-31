package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.server.model.NewOrder;
import org.springframework.data.repository.CrudRepository;

public interface NewOrderRepository extends CrudRepository<NewOrder, Long> {}
