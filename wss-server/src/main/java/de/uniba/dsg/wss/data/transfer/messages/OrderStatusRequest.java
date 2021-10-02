package de.uniba.dsg.wss.data.transfer.messages;

import javax.validation.constraints.NotBlank;

public class OrderStatusRequest {

  @NotBlank(message = "Warehouse id is required")
  private String warehouseId;

  @NotBlank(message = "District id is required")
  private String districtId;

  private String customerId;

  private String customerEmail;

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

  public String getCustomerEmail() {
    return customerEmail;
  }

  public void setCustomerEmail(String customerEmail) {
    this.customerEmail = customerEmail;
  }
}
