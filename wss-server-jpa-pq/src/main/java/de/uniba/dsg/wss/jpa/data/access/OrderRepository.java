package de.uniba.dsg.wss.jpa.data.access;

import de.uniba.dsg.wss.jpa.data.model.OrderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA repository for accessing and modifying {@link OrderEntity orders}.
 *
 * @author Benedikt Full
 */
@Transactional(readOnly = true)
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

  List<OrderEntity> findByDistrictId(String districtId);

  @Query(
      value =
          "SELECT * FROM orders WHERE customer_id = :customerId ORDER BY entrydate DESC LIMIT 1",
      nativeQuery = true)
  Optional<OrderEntity> findMostRecentOrderOfCustomer(String customerId);

  @Query(
      value =
          "SELECT * FROM orders WHERE fulfilled = false AND district_id = :districtId ORDER BY entrydate ASC LIMIT 1",
      nativeQuery = true)
  Optional<OrderEntity> findOldestUnfulfilledOrderOfDistrict(String districtId);

  @Query(
      value =
          "SELECT * FROM orders WHERE district_id = :districtId ORDER BY entrydate DESC LIMIT 20",
      nativeQuery = true)
  List<OrderEntity> findTwentyMostRecentOrdersOfDistrict(String districtId);
}