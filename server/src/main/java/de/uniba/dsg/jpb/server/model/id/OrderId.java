package de.uniba.dsg.jpb.server.model.id;

import de.uniba.dsg.jpb.server.model.District;
import java.io.Serializable;
import java.util.Objects;

public class OrderId implements Serializable {

  private static final long serialVersionUID = 2488174292033100196L;
  private Long id;
  private District district;

  public OrderId() {
    id = 0L;
    district = null;
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderId orderId = (OrderId) o;
    return id.equals(orderId.id) && district.equals(orderId.district);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, district);
  }
}
