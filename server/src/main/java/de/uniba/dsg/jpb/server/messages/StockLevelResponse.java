package de.uniba.dsg.jpb.server.messages;

public class StockLevelResponse {

  private Long warehouseId;
  private Long districtId;
  private int stockThreshold;
  private int lowStocksCount;

  public StockLevelResponse(StockLevelRequest req) {
    warehouseId = req.getWarehouseId();
    districtId = req.getDistrictId();
    stockThreshold = req.getStockThreshold();
  }

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

  public int getLowStocksCount() {
    return lowStocksCount;
  }

  public void setLowStocksCount(int lowStocksCount) {
    this.lowStocksCount = lowStocksCount;
  }
}
