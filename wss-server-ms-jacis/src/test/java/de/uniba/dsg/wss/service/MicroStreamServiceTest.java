package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.gen.DefaultDataGenerator;
import de.uniba.dsg.wss.data.gen.MsDataConverter;
import de.uniba.dsg.wss.data.gen.MsDataWriter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MicroStreamTestConfiguration.class)
public abstract class MicroStreamServiceTest {

  public void populateStorage(DefaultDataGenerator generator, MsDataWriter dataWriter) {
    dataWriter.write(new MsDataConverter().convert(generator.generate()));
  }
}
