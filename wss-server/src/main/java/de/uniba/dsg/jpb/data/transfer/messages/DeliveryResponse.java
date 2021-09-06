package de.uniba.dsg.jpb.data.transfer.messages;

public class DeliveryResponse {

  private String warehouseId;

  private String carrierId;

  public DeliveryResponse() {}

  public DeliveryResponse(DeliveryRequest req) {
    warehouseId = req.getWarehouseId();
    carrierId = req.getCarrierId();
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
