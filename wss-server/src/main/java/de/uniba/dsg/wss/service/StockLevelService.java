package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;

/**
 * This service implements the stock level business transaction.
 *
 * <p>In this transaction, the stocks affected by the 20 most recent orders of a specified district
 * are checked for whether they are below a certain threshold. The number of all products with a
 * stock quantity value below this threshold is returned as part of the service response. Threshold
 * and district are specified as part of the request.
 *
 * @author Benedikt Full
 */
public abstract class StockLevelService
    implements TransactionService<StockLevelRequest, StockLevelResponse> {

  public StockLevelService() {}
}
