package de.uniba.dsg.jpb.data.transfer.messages;

public class DeliveryResponse {

  private Long warehouseId;
  private Long carrierId;

  public DeliveryResponse() {}

  public DeliveryResponse(DeliveryRequest req) {
    warehouseId = req.getWarehouseId();
    carrierId = req.getCarrierId();
  }

  public Long getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(Long warehouseId) {
    this.warehouseId = warehouseId;
  }

  public Long getCarrierId() {
    return carrierId;
  }

  public void setCarrierId(Long carrierId) {
    this.carrierId = carrierId;
  }
}
