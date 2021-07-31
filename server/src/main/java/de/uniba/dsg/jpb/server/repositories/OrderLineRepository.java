package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.server.model.OrderLine;
import org.springframework.data.repository.CrudRepository;

public interface OrderLineRepository extends CrudRepository<OrderLine, Long> {}
