package de.uniba.dsg.jpb.server.service;

import de.uniba.dsg.jpb.server.messages.StockLevelRequest;
import de.uniba.dsg.jpb.server.messages.StockLevelResponse;

public abstract class StockLevelService
    implements TransactionService<StockLevelRequest, StockLevelResponse> {

  public StockLevelService() {}
}
