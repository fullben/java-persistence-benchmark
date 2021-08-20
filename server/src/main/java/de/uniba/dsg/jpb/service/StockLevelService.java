package de.uniba.dsg.jpb.service;

import de.uniba.dsg.jpb.messages.StockLevelRequest;
import de.uniba.dsg.jpb.messages.StockLevelResponse;

public abstract class StockLevelService
    implements TransactionService<StockLevelRequest, StockLevelResponse> {

  public StockLevelService() {}
}
