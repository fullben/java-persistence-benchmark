package de.uniba.dsg.jpb.data.transfer.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockLevelRequest {

  @JsonProperty(required = true)
  private Long warehouseId;

  @JsonProperty(required = true)
  private Long districtId;

  @JsonProperty(required = true)
  private int stockThreshold;

  public Long getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(Long warehouseId) {
    this.warehouseId = warehouseId;
  }

  public Long getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Long districtId) {
    this.districtId = districtId;
  }

  public int getStockThreshold() {
    return stockThreshold;
  }

  public void setStockThreshold(int stockThreshold) {
    this.stockThreshold = stockThreshold;
  }
}
