package de.uniba.dsg.wss.service;

import de.uniba.dsg.wss.data.gen.MsDataConverter;
import de.uniba.dsg.wss.data.gen.MsDataWriter;
import de.uniba.dsg.wss.data.gen.TestDataGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MicroStreamTestConfiguration.class)
public abstract class MicroStreamServiceTest {

  @Autowired private MsDataWriter dataWriter;

  public void populateStorage() {
    TestDataGenerator generator = new TestDataGenerator();
    generator.generate();
    MsDataConverter converter = new MsDataConverter();
    converter.convert(generator);
    dataWriter.writeAll(converter);
  }
}
