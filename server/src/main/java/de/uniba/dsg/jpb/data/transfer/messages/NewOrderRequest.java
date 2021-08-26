package de.uniba.dsg.jpb.data.transfer.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class NewOrderRequest {

  @JsonProperty(required = true)
  private String warehouseId;

  @JsonProperty(required = true)
  private String districtId;

  @JsonProperty(required = true)
  private String customerId;

  @JsonProperty(required = true)
  private List<NewOrderRequestItem> items;

  public String getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(String warehouseId) {
    this.warehouseId = warehouseId;
  }

  public String getDistrictId() {
    return districtId;
  }

  public void setDistrictId(String districtId) {
    this.districtId = districtId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public List<NewOrderRequestItem> getItems() {
    return items;
  }

  public void setItems(List<NewOrderRequestItem> items) {
    this.items = items;
  }
}
