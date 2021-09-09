package de.uniba.dsg.wss.data.gen.jpa;

import de.uniba.dsg.wss.data.gen.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * If the server is launched in JPA persistence mode, this initializer generates a data model based
 * on the associated configuration properties and writes it to the configured JPA-based persistence
 * solution.
 *
 * @author Benedikt Full
 */
@Component
@ConditionalOnExpression("'${jpb.persistence.mode}' == 'jpa' and '${jpb.model.initialize}'")
public class JpaDataInitializer extends DataInitializer {

  private final JpaDataWriter databaseWriter;

  @Autowired
  public JpaDataInitializer(
      Environment environment, PasswordEncoder passwordEncoder, JpaDataWriter databaseWriter) {
    super(environment, passwordEncoder);
    this.databaseWriter = databaseWriter;
  }

  @Override
  public void run(String... args) {
    JpaDataGenerator jpaDataGenerator = createJpaDataGenerator();
    jpaDataGenerator.generate();
    databaseWriter.writeAll(jpaDataGenerator);
  }
}
