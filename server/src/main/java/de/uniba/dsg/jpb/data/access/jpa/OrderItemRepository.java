package de.uniba.dsg.jpb.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.OrderItemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

  // TODO this method might not be necessary in @Transactional context, see https://www.baeldung.com/java-jpa-lazy-collections (5.)
  List<OrderItemEntity> findByOrderIdOrderByNumberAsc(Long orderId);
}
