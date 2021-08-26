package de.uniba.dsg.jpb.data.transfer.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockLevelRequest {

  @JsonProperty(required = true)
  private String warehouseId;

  @JsonProperty(required = true)
  private String districtId;

  @JsonProperty(required = true)
  private int stockThreshold;

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
