package de.uniba.dsg.wss.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import de.uniba.dsg.wss.api.MsResourceController;
import de.uniba.dsg.wss.data.transfer.representations.DistrictRepresentation;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class MsResourceControllerTest extends MicroStreamServiceTest {

  @Autowired private MsResourceController controller;

  @BeforeEach
  public void setUp() {
    prepareTestStorage();
  }

  @Test
  public void checkWarehouseDistricts() {
    ResponseEntity<List<DistrictRepresentation>> districts = controller.getWarehouseDistricts("W0");
    assertEquals(2, districts.getBody().size());
    assertFalse(districts.getBody().stream().noneMatch(d -> "D0".equals(d.getId())));
  }
}
