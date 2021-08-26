package de.uniba.dsg.jpb.data.transfer.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderStatusRequest {

  @JsonProperty(required = true)
  private String warehouseId;

  @JsonProperty(required = true)
  private String districtId;

  @JsonProperty(required = false)
  private String customerId;

  @JsonProperty(required = false)
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
