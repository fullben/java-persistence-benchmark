package de.uniba.dsg.jpb.data.model.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private DistrictEntity district;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private CustomerEntity customer;

  private LocalDateTime entryDate;

  @ManyToOne(fetch = FetchType.EAGER)
  private CarrierEntity carrier;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItemEntity> items;

  private int itemCount;
  private boolean allLocal;
  private boolean fulfilled;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderEntity that = (OrderEntity) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
