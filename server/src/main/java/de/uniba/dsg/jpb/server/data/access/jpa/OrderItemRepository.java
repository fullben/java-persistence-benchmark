package de.uniba.dsg.jpb.server.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.OrderItemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

  List<OrderItemEntity> findByOrderIdOrderByNumberAsc(Long orderId);
}
