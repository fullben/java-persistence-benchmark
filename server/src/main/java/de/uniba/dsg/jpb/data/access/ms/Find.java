package de.uniba.dsg.jpb.data.access.ms;

import static java.util.Objects.requireNonNull;

import de.uniba.dsg.jpb.data.model.ms.CarrierData;
import de.uniba.dsg.jpb.data.model.ms.CustomerData;
import de.uniba.dsg.jpb.data.model.ms.DistrictData;
import de.uniba.dsg.jpb.data.model.ms.OrderData;
import de.uniba.dsg.jpb.data.model.ms.ProductData;
import de.uniba.dsg.jpb.data.model.ms.StockData;
import de.uniba.dsg.jpb.data.model.ms.WarehouseData;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class which provides implementations for common search or find operations that are
 * performed on the data model as part of the MicroStream implementations for the business
 * transactions.
 *
 * <p>Aside from reducing the amount of duplicate code, this class allows for consistent behavior in
 * case of certain common scenarios:
 *
 * <ul>
 *   <li>All object parameters provided to any of the methods in this class that are not identifiers
 *       are validated for not being {@code null}
 *   <li>Identifiers may be {@code null}
 *   <li>If a method's purpose is to find an instance of some class based on an identifier, the
 *       method will throw a {@link DataNotFoundException} if it fails to find such an instance
 * </ul>
 *
 * @author Benedikt Full
 */
public final class Find {

  private Find() {
    throw new AssertionError();
  }

  public static ProductData productById(String id, Collection<ProductData> products) {
    requireNonNull(products);
    return products.parallelStream()
        .filter(p -> p.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static CarrierData carrierById(String id, Collection<CarrierData> carriers) {
    requireNonNull(carriers);
    return carriers.parallelStream()
        .filter(c -> c.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static WarehouseData warehouseById(String id, Collection<WarehouseData> warehouses) {
    requireNonNull(warehouses);
    return warehouses.stream()
        .filter(w -> w.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static DistrictData districtById(String id, WarehouseData warehouse) {
    requireNonNull(warehouse);
    return warehouse.getDistricts().stream()
        .filter(d -> d.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static CustomerData customerById(String id, DistrictData district) {
    requireNonNull(district);
    return district.getCustomers().parallelStream()
        .filter(c -> c.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static CustomerData customerByEmail(String email, DistrictData district) {
    requireNonNull(district);
    return district.getCustomers().parallelStream()
        .filter(c -> c.getEmail().equals(email))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static OrderData orderById(String id, DistrictData district) {
    requireNonNull(district);
    return district.getOrders().parallelStream()
        .filter(o -> o.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static Optional<OrderData> mostRecentOrderOfCustomer(String id, DistrictData district) {
    requireNonNull(district);
    return district.getOrders().parallelStream()
        .filter(o -> o.getCustomer().getId().equals(id))
        .max(Comparator.comparing(OrderData::getEntryDate));
  }

  public static Optional<OrderData> oldestUnfulfilledOrderOfDistrict(DistrictData district) {
    requireNonNull(district);
    return district.getOrders().parallelStream()
        .filter(o -> !o.isFulfilled())
        .min(Comparator.comparing(OrderData::getEntryDate));
  }

  public static List<OrderData> twentyMostRecentOrdersOfDistrict(DistrictData district) {
    requireNonNull(district);
    return district.getOrders().stream()
        .sorted(Comparator.comparing(OrderData::getEntryDate))
        .limit(20)
        .collect(Collectors.toList());
  }

  public static List<StockData> stocksByProductIdsAndQuantityThreshold(
      Collection<String> productIds, int quantityThreshold, Collection<StockData> stocks) {
    requireNonNull(productIds);
    if (quantityThreshold < 1) {
      throw new IllegalArgumentException();
    }
    requireNonNull(stocks);
    return stocks.parallelStream()
        .filter(
            s -> productIds.contains(s.getProduct().getId()) && s.getQuantity() < quantityThreshold)
        .collect(Collectors.toList());
  }
}
