package de.uniba.dsg.jpb.data.model.ms;

import java.time.LocalDateTime;
import java.util.List;
import one.microstream.reference.Lazy;

public class OrderData {

  private Long id;
  private DistrictData district;
  private CustomerData customer;
  private LocalDateTime entryDate;
  private Long carrierId;
  private Lazy<List<OrderItemData>> items;
  private int itemCount;
  private boolean allLocal;
  private boolean fulfilled;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DistrictData getDistrict() {
    return district;
  }

  public void setDistrict(DistrictData district) {
    this.district = district;
  }

  public CustomerData getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerData customer) {
    this.customer = customer;
  }

  public LocalDateTime getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(LocalDateTime entryDate) {
    this.entryDate = entryDate;
  }

  public Long getCarrierId() {
    return carrierId;
  }

  public void setCarrierId(Long carrierId) {
    this.carrierId = carrierId;
  }

  public List<OrderItemData> getItems() {
    return Lazy.get(items);
  }

  public void setItems(List<OrderItemData> items) {
    this.items = Lazy.Reference(items);
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
