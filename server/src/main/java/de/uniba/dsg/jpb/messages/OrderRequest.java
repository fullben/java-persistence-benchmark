package de.uniba.dsg.jpb.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OrderRequest {

  @JsonProperty(required = true)
  private Long warehouseId;

  @JsonProperty(required = true)
  private Long districtId;

  @JsonProperty(required = true)
  private Long customerId;

  @JsonProperty(required = true)
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
