package de.uniba.dsg.jpb.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.OrderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

  List<OrderEntity> findByDistrictId(Long districtId);

  @Query(
      value =
          "SELECT * FROM orders o WHERE o.customer_id = :customerId ORDER BY o.entrydate DESC LIMIT 1",
      nativeQuery = true)
  Optional<OrderEntity> findMostRecentOrderOfCustomer(Long customerId);

  @Query(
      value =
          "SELECT * FROM orders o WHERE o.district_id = :districtId AND o.fulfilled = false ORDER BY o.entrydate ASC LIMIT 1",
      nativeQuery = true)
  Optional<OrderEntity> findOldestUnfulfilledOrderOfDistrict(Long districtId);

  @Query(
      value =
          "SELECT * FROM orders o WHERE o.district_id = :districtId ORDER BY o.entrydate DESC LIMIT 20",
      nativeQuery = true)
  List<OrderEntity> find20MostRecentOrdersOfDistrict(Long districtId);
}
