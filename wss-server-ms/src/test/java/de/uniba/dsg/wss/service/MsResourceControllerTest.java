package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.api.MsResourceController;
import de.uniba.dsg.wss.data.transfer.representations.DistrictRepresentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MsResourceControllerTest extends MicroStreamServiceTest{

    @Autowired
    private MsResourceController controller;

    @BeforeEach
    public void setUp() {
        prepareTestStorage();
    }

    @Test
    public void checkWarehouseDistricts() {
        ResponseEntity<List<DistrictRepresentation>> districts = controller.getWarehouseDistricts("W0");
        assertEquals(2, districts.getBody().size());
        assertTrue(!districts.getBody().stream().filter(d -> "D0".equals(d.getId())).collect(Collectors.toList()).isEmpty());
    }

}
