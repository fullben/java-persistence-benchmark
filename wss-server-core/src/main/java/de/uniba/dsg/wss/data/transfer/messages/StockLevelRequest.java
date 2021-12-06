package de.uniba.dsg.wss.data.transfer.messages;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class StockLevelRequest {

  @NotBlank(message = "Warehouse id is required")
  private String warehouseId;

  @NotBlank(message = "District id is required")
  private String districtId;

  @Min(value = 10, message = "Stock level threshold must be greater than nine")
  private int stockThreshold;

  public StockLevelRequest(){

  }

  public StockLevelRequest(String warehouseId, String districtId, int stockThreshold) {
    this.warehouseId = warehouseId;
    this.districtId = districtId;
    this.stockThreshold = stockThreshold;
  }

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

  public int getStockThreshold() {
    return stockThreshold;
  }

  public void setStockThreshold(int stockThreshold) {
    this.stockThreshold = stockThreshold;
  }
}
