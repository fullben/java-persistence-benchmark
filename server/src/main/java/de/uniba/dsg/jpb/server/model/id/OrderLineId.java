package de.uniba.dsg.jpb.server.model.id;

import de.uniba.dsg.jpb.server.model.Order;
import java.io.Serializable;
import java.util.Objects;

public class OrderLineId implements Serializable {

  private static final long serialVersionUID = -6634073103952830515L;
  private Order order;
  private int number;

  public OrderLineId() {
    order = null;
    number = 0;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderLineId that = (OrderLineId) o;
    return number == that.number && order.equals(that.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(order, number);
  }
}
