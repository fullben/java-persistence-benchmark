package de.uniba.dsg.wss.data.transfer.messages;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class PaymentRequest {

  @NotBlank(message = "Warehouse id is required")
  private String warehouseId;

  @NotBlank(message = "District id is required")
  private String districtId;

  private String customerId;

  private String customerEmail;

  @Min(value = 1, message = "Amount must be greater than zero")
  private double amount;

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

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }
}
