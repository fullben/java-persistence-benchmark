package de.uniba.dsg.wss.data.transfer.messages;

import javax.validation.constraints.NotBlank;

public class DeliveryRequest {

  @NotBlank(message = "Warehouse id is required")
  private String warehouseId;

  @NotBlank(message = "Carrier id is required")
  private String carrierId;

  public DeliveryRequest(){

  }

  public DeliveryRequest(String warehouseId, String carrierId) {
    this.warehouseId = warehouseId;
    this.carrierId = carrierId;
  }

  public String getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(String warehouseId) {
    this.warehouseId = warehouseId;
  }

  public String getCarrierId() {
    return carrierId;
  }

  public void setCarrierId(String carrierId) {
    this.carrierId = carrierId;
  }
}
