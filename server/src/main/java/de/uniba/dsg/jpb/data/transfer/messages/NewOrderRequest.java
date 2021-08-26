package de.uniba.dsg.jpb.data.transfer.messages;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class NewOrderRequest {

  @NotBlank(message = "Warehouse id is required")
  private String warehouseId;

  @NotBlank(message = "District id is required")
  private String districtId;

  @NotBlank(message = "Customer id is required")
  private String customerId;

  @NotEmpty(message = "Order items are required")
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
