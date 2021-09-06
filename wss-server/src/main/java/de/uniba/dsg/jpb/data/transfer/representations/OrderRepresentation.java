package de.uniba.dsg.jpb.data.transfer.representations;

import java.time.LocalDateTime;
import java.util.List;

public class OrderRepresentation {

  private String id;
  private DistrictRepresentation district;
  private CustomerRepresentation customer;
  private LocalDateTime entryDate;
  private CarrierRepresentation carrier;
  private List<OrderItemRepresentation> items;
  private int itemCount;
  private boolean allLocal;
  private boolean fulfilled;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public DistrictRepresentation getDistrict() {
    return district;
  }

  public void setDistrict(DistrictRepresentation district) {
    this.district = district;
  }

  public CustomerRepresentation getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerRepresentation customer) {
    this.customer = customer;
  }

  public LocalDateTime getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(LocalDateTime entryDate) {
    this.entryDate = entryDate;
  }

  public CarrierRepresentation getCarrier() {
    return carrier;
  }

  public void setCarrier(CarrierRepresentation carrier) {
    this.carrier = carrier;
  }

  public List<OrderItemRepresentation> getItems() {
    return items;
  }

  public void setItems(List<OrderItemRepresentation> items) {
    this.items = items;
  }

  public int getItemCount() {
    return itemCount;
  }

  public void setItemCount(int itemCount) {
    this.itemCount = itemCount;
  }

  public boolean isAllLocal() {
    return allLocal;
  }

  public void setAllLocal(boolean allLocal) {
    this.allLocal = allLocal;
  }

  public boolean isFulfilled() {
    return fulfilled;
  }

  public void setFulfilled(boolean fulfilled) {
    this.fulfilled = fulfilled;
  }
}
