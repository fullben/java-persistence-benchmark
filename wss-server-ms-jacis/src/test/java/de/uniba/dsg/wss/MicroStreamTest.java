package de.uniba.dsg.wss;

import de.uniba.dsg.wss.data.gen.MsDataConverter;
import de.uniba.dsg.wss.data.gen.MsDataWriter;
import de.uniba.dsg.wss.data.gen.TestDataGenerator;
import de.uniba.dsg.wss.service.MicroStreamTestConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MicroStreamTestConfiguration.class)
public abstract class MicroStreamTest {

  @Autowired private MsDataWriter dataWriter;

  public void populateStorage() {
    dataWriter.write(new MsDataConverter().convert(new TestDataGenerator().generate()));
  }
}
