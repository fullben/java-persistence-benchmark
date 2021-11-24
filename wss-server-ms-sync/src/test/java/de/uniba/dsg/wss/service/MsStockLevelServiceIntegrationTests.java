package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.uniba.dsg.wss.data.transfer.messages.StockLevelRequest;
import de.uniba.dsg.wss.data.transfer.messages.StockLevelResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MsStockLevelServiceIntegrationTests extends MicroStreamServiceTest {

  @Autowired private MsStockLevelService stockLevelService;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
  }

  @Test
  public void testStockLevelLimit() {
    StockLevelRequest request = new StockLevelRequest("W0", "D0", 100);
    StockLevelResponse response = stockLevelService.process(request);

    assertEquals(1, response.getLowStocksCount());
  }
}
