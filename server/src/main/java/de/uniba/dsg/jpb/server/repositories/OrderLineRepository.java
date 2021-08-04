package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.server.model.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {}
