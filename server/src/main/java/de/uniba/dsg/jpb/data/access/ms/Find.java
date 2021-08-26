package de.uniba.dsg.jpb.data.access.ms;

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

public final class Find {

  private Find() {
    throw new AssertionError();
  }

  public static ProductData productById(String id, Collection<ProductData> products) {
    return products.parallelStream()
        .filter(p -> p.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static CarrierData carrierById(String id, Collection<CarrierData> carriers) {
    return carriers.parallelStream()
        .filter(c -> c.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static WarehouseData warehouseById(String id, Collection<WarehouseData> warehouses) {
    return warehouses.stream()
        .filter(w -> w.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static DistrictData districtById(String id, WarehouseData warehouse) {
    return warehouse.getDistricts().stream()
        .filter(d -> d.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static CustomerData customerById(String id, DistrictData district) {
    return district.getCustomers().parallelStream()
        .filter(c -> c.getId().equals(id))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static CustomerData customerByEmail(String email, DistrictData district) {
    return district.getCustomers().parallelStream()
        .filter(c -> c.getEmail().equals(email))
        .findAny()
        .orElseThrow(DataNotFoundException::new);
  }

  public static Optional<OrderData> mostRecentOrderOfCustomer(String id, DistrictData district) {
    return district.getOrders().parallelStream()
        .filter(o -> o.getCustomer().getId().equals(id))
        .max(Comparator.comparing(OrderData::getEntryDate));
  }

  public static Optional<OrderData> oldestUnfulfilledOrderOfDistrict(DistrictData district) {
    return district.getOrders().parallelStream()
        .filter(o -> !o.isFulfilled())
        .min(Comparator.comparing(OrderData::getEntryDate));
  }

  public static List<OrderData> twentyMostRecentOrdersOfDistrict(DistrictData district) {
    // TODO verify that these are the MOST RECENT orders
    return district.getOrders().stream()
        .sorted(Comparator.comparing(OrderData::getEntryDate))
        .limit(20)
        .collect(Collectors.toList());
  }

  public static List<StockData> stocksByProductIdsAndQuantityThreshold(
      Collection<String> productIds, int quantityThreshold, Collection<StockData> stocks) {
    return stocks.parallelStream()
        .filter(
            s -> productIds.contains(s.getProduct().getId()) && s.getQuantity() < quantityThreshold)
        .collect(Collectors.toList());
  }
}
