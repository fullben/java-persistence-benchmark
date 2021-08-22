package de.uniba.dsg.jpb.transaction;

import de.uniba.dsg.jpb.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.jpb.data.transfer.messages.StockLevelResponse;

public abstract class StockLevelService
    implements TransactionService<StockLevelRequest, StockLevelResponse> {

  public StockLevelService() {}
}
