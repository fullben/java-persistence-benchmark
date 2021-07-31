package de.uniba.dsg.jpb.server.model.id;

import de.uniba.dsg.jpb.server.model.District;
import java.io.Serializable;
import java.util.Objects;

public class CustomerId implements Serializable {

  private static final long serialVersionUID = 6755572995471579912L;
  private Long id;
  private District district;

  public CustomerId() {
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
    CustomerId that = (CustomerId) o;
    return id.equals(that.id) && district.equals(that.district);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, district);
  }
}
