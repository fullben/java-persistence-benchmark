package de.uniba.dsg.wss.ms.service;

import de.uniba.dsg.wss.data.gen.DataGenerator;
import de.uniba.dsg.wss.ms.data.gen.MsDataConverter;
import de.uniba.dsg.wss.ms.data.gen.MsDataWriter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MicroStreamTestConfiguration.class)
public abstract class MicroStreamServiceTest {

  public void populateStorage(DataGenerator generator, MsDataWriter dataWriter) {
    generator.generate();
    MsDataConverter converter = new MsDataConverter();
    converter.convert(generator);
    dataWriter.writeAll(converter);
  }
}
