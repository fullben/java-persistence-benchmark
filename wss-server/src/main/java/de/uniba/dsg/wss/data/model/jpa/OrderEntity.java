package de.uniba.dsg.wss.data.model.jpa;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * An order issued by a {@link CustomerEntity Customer} for a certain amount of {@link ProductEntity
 * Products}.
 *
 * @see OrderItemEntity
 * @author Benedikt Full
 */
@Entity(name = "Order")
@Table(
    name = "orders",
    indexes = {
      @Index(name = "orders_idx_entrydate", columnList = "entrydate"),
      @Index(name = "orders_idx_customer_id", columnList = "customer_id"),
      @Index(name = "orders_idx_district_id", columnList = "district_id")
    })
public class OrderEntity extends BaseEntity {

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private DistrictEntity district;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private CustomerEntity customer;

  @Column(nullable = false, name = "entrydate")
  private LocalDateTime entryDate;

  @ManyToOne(fetch = FetchType.EAGER)
  private CarrierEntity carrier;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItemEntity> items;

  private int itemCount;
  private boolean allLocal;
  private boolean fulfilled;

  public DistrictEntity getDistrict() {
    return district;
  }

  public void setDistrict(DistrictEntity district) {
    this.district = district;
  }

  public CustomerEntity getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerEntity customer) {
    this.customer = customer;
  }

  public LocalDateTime getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(LocalDateTime entryDate) {
    this.entryDate = entryDate;
  }

  public CarrierEntity getCarrier() {
    return carrier;
  }

  public void setCarrier(CarrierEntity carrier) {
    this.carrier = carrier;
  }

  public List<OrderItemEntity> getItems() {
    return items;
  }

  public void setItems(List<OrderItemEntity> items) {
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
