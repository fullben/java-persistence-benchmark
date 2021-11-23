package de.uniba.dsg.wss.data.transfer.messages;

public class StockLevelResponse {

  private String warehouseId;
  private String districtId;
  private int stockThreshold;
  private int lowStocksCount;

  public StockLevelResponse(StockLevelRequest req) {
    warehouseId = req.getWarehouseId();
    districtId = req.getDistrictId();
    stockThreshold = req.getStockThreshold();
  }

  public StockLevelResponse(StockLevelRequest req, int lowStocksCount) {
    warehouseId = req.getWarehouseId();
    districtId = req.getDistrictId();
    stockThreshold = req.getStockThreshold();
    this.lowStocksCount = lowStocksCount;
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

  public int getLowStocksCount() {
    return lowStocksCount;
  }

  public void setLowStocksCount(int lowStocksCount) {
    this.lowStocksCount = lowStocksCount;
  }
}
