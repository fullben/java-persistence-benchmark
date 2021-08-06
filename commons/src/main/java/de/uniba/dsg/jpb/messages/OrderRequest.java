package de.uniba.dsg.jpb.messages;

import java.util.List;

public class OrderRequest {

  private Long warehouseId;
  private Long districtId;
  private Long customerId;
  private List<OrderRequestItem> items;

  public Long getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(Long warehouseId) {
    this.warehouseId = warehouseId;
  }

  public Long getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Long districtId) {
    this.districtId = districtId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public List<OrderRequestItem> getItems() {
    return items;
  }

  public void setItems(List<OrderRequestItem> items) {
    this.items = items;
  }
}
