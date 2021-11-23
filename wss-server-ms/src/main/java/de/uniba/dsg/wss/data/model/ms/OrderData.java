package de.uniba.dsg.wss.data.model.ms;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An order issued by a {@link CustomerData customer} for a certain amount of {@link ProductData
 * products}.
 *
 * @see OrderItemData
 * @author Benedikt Full
 */
public class OrderData extends BaseData implements Comparable<OrderData>{

  private final DistrictData districtRef;
  private final CustomerData customerRef;
  private CarrierData carrierRef;

  private final LocalDateTime entryDate;
  private final int itemCount;
  private final boolean allLocal;
  private boolean fulfilled;

  private final List<OrderItemData> items;

  public OrderData(DistrictData districtRef, CustomerData customerRef, LocalDateTime entryDate, int itemCount, boolean allLocal) {
    super();
    this.districtRef = districtRef;
    this.customerRef = customerRef;
    this.entryDate = entryDate;
    this.itemCount = itemCount;
    this.allLocal = allLocal;
    this.fulfilled = false;
    this.items = new ArrayList<>();
  }

  // JPA conversion constructor
  public OrderData(String id, DistrictData districtRef, CustomerData customerRef, CarrierData carrierRef, LocalDateTime entryDate, int itemCount, boolean allLocal, boolean fulfilled) {
    super(id);
    this.districtRef = districtRef;
    this.customerRef = customerRef;
    this.carrierRef = carrierRef;
    this.entryDate = entryDate;
    this.itemCount = itemCount;
    this.allLocal = allLocal;
    this.fulfilled = fulfilled;
    this.items = new ArrayList<>();
  }

  public DistrictData getDistrictRef() {
    return districtRef;
  }

  public CustomerData getCustomerRef() {
    return customerRef;
  }

  public LocalDateTime getEntryDate() {
    return entryDate;
  }

  public int getItemCount() {
    return itemCount;
  }

  public boolean isAllLocal() {
    return allLocal;
  }

  public List<OrderItemData> getItems() {
    return items;
  }

  @Override
  public int compareTo(OrderData o) {
    return this.entryDate.compareTo(o.entryDate);
  }

  public CarrierData getCarrierRef() {
    synchronized (this.id) {
      return carrierRef;
    }
  }

  public void updateCarrier(CarrierData carrier) {
    synchronized (this.id) {
      this.carrierRef = carrier;
    }
  }

  public void setAsFulfilled() {
    synchronized (this.id) {
      this.fulfilled = true;
    }
  }

  public boolean isNotFulfilled(){
    synchronized (this.id) {
      return !fulfilled;
    }
  }

  public boolean isFulfilled() {
    synchronized (this.id) {
      return fulfilled;
    }
  }
}
