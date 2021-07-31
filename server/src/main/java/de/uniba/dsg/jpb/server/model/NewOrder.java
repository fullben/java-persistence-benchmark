package de.uniba.dsg.jpb.server.model;

import de.uniba.dsg.jpb.server.model.id.NewOrderId;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;

@Entity
@IdClass(NewOrderId.class)
public class NewOrder {

  @Id
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(name = "order_id", referencedColumnName = "id"),
    @JoinColumn(name = "order_district_id", referencedColumnName = "district_id"),
    @JoinColumn(
        name = "order_district_warehouse_id",
        referencedColumnName = "district_warehouse_id")
  })
  private Order order;

  public NewOrder() {}

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NewOrder newOrder = (NewOrder) o;
    return order.equals(newOrder.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(order);
  }
}
