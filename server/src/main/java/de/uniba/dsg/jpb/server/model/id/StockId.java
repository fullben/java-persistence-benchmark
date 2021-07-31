package de.uniba.dsg.jpb.server.model.id;

import de.uniba.dsg.jpb.server.model.Item;
import de.uniba.dsg.jpb.server.model.Warehouse;
import java.io.Serializable;
import java.util.Objects;

public class StockId implements Serializable {

  private static final long serialVersionUID = -141471023345683991L;
  private Item item;
  private Warehouse warehouse;

  public StockId() {
    item = null;
    warehouse = null;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
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
    StockId stockKey = (StockId) o;
    return item.equals(stockKey.item) && warehouse.equals(stockKey.warehouse);
  }

  @Override
  public int hashCode() {
    return Objects.hash(item, warehouse);
  }
}
