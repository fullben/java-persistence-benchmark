package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.gen.jpa.JpaDataGenerator;
import de.uniba.dsg.wss.data.gen.ms.JpaToMsConverter;
import de.uniba.dsg.wss.data.gen.ms.MsDataWriter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MicroStreamTestConfiguration.class)
public abstract class MicroStreamServiceTest {

  public void populateStorage(JpaDataGenerator generator, MsDataWriter dataWriter) {
    generator.generate();
    JpaToMsConverter converter = new JpaToMsConverter(generator);
    converter.convert();
    dataWriter.writeAll(converter);
  }
}
