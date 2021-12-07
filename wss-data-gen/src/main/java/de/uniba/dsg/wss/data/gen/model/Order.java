package de.uniba.dsg.wss.data.gen.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * An order issued by a {@link Customer customer} for a certain amount of {@link Product products}.
 *
 * @see OrderItem
 * @author Benedikt Full
 */
public class Order extends Base {

  private District district;
  private Customer customer;
  private LocalDateTime entryDate;
  private Carrier carrier;
  private List<OrderItem> items;
  private int itemCount;
  private boolean allLocal;
  private boolean fulfilled;

  public District getDistrict() {
    return district;
  }

  public void setDistrict(District district) {
    this.district = district;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public LocalDateTime getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(LocalDateTime entryDate) {
    this.entryDate = entryDate;
  }

  public Carrier getCarrier() {
    return carrier;
  }

  public void setCarrier(Carrier carrier) {
    this.carrier = carrier;
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public void setItems(List<OrderItem> items) {
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
