package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.model.StockData;

/**
 * Implements an additional internal DTO.
 *
 * @author Johannes Manner
 */
public class StockUpdateDto {

  private final StockData stockData;
  private final int quantity;

  public StockUpdateDto(StockData stockData, int quantity) {
    this.stockData = stockData;
    this.quantity = quantity;
  }

  public StockData getStockData() {
    return stockData;
  }

  public int getQuantity() {
    return quantity;
  }
}
