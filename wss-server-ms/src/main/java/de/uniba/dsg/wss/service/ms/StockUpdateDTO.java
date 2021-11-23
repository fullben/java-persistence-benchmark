package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.model.ms.StockData;

/**
 * Implements an additional internal DTO.
 *
 * @author Johannes Manner
 */
public class StockUpdateDTO {

    private final StockData stockData;
    private final int quantity;

    public StockUpdateDTO(StockData stockData, int quantity) {
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
