package de.uniba.dsg.jpb.server.model.id;

import de.uniba.dsg.jpb.server.model.Order;
import java.io.Serializable;
import java.util.Objects;

public class NewOrderId implements Serializable {

  private static final long serialVersionUID = 7015110763860994072L;
  private Order order;

  public NewOrderId() {
    order = null;
  }

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
    NewOrderId that = (NewOrderId) o;
    return order.equals(that.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(order);
  }
}
