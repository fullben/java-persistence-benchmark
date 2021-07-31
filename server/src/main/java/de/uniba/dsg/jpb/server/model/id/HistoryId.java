package de.uniba.dsg.jpb.server.model.id;

import de.uniba.dsg.jpb.server.model.Customer;
import java.io.Serializable;
import java.util.Objects;

public class HistoryId implements Serializable {

  private static final long serialVersionUID = -8318984087481532961L;
  private Customer customer;

  public HistoryId() {
    customer = null;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HistoryId historyId = (HistoryId) o;
    return customer.equals(historyId.customer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customer);
  }
}
