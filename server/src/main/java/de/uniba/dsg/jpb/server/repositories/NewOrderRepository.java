package de.uniba.dsg.jpb.server.repositories;

import de.uniba.dsg.jpb.server.model.NewOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewOrderRepository extends JpaRepository<NewOrder, Long> {}
