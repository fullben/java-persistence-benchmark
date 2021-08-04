package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
