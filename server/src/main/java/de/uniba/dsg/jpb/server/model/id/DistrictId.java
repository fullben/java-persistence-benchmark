package de.uniba.dsg.jpb.server.model.id;

import de.uniba.dsg.jpb.server.model.Warehouse;
import java.io.Serializable;
import java.util.Objects;

public class DistrictId implements Serializable {

  private static final long serialVersionUID = 1771164293179631154L;
  private Long id;
  private Warehouse warehouse;

  public DistrictId() {
    id = 0L;
    warehouse = null;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Warehouse getWarehouse() {
    return warehouse;
  }

  public void setWarehouse(Warehouse warehouse) {
    this.warehouse = warehouse;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DistrictId that = (DistrictId) o;
    return id.equals(that.id) && warehouse.equals(that.warehouse);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, warehouse);
  }
}
