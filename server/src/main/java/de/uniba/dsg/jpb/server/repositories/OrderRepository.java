package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.server.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {}
