package de.uniba.dsg.jpb.server.model;

import de.uniba.dsg.jpb.server.model.id.OrderId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "oorder")
@IdClass(OrderId.class)
public class Order {

  @Id private Long id;

  @Id
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private District district;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Customer customer;

  private LocalDateTime entryDate;
  private Long carrierId;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderLine> orderLines;

  private int orderLineCount;
  private int allLocal;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public Long getCarrierId() {
    return carrierId;
  }

  public void setCarrierId(Long carrierId) {
    this.carrierId = carrierId;
  }

  public List<OrderLine> getOrderLines() {
    return orderLines;
  }

  public void setOrderLines(List<OrderLine> orderLines) {
    this.orderLines = orderLines;
  }

  public int getOrderLineCount() {
    return orderLineCount;
  }

  public void setOrderLineCount(int orderLineCount) {
    this.orderLineCount = orderLineCount;
  }

  public int getAllLocal() {
    return allLocal;
  }

  public void setAllLocal(int allLocal) {
    this.allLocal = allLocal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Order order = (Order) o;
    return id.equals(order.id) && district.equals(order.district);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, district);
  }
}
