package de.uniba.dsg.jpb.server;

import de.uniba.dsg.jpb.server.data.gen.JpaDataGenerator;
import de.uniba.dsg.jpb.server.data.gen.JpaDatabaseWriter;
import de.uniba.dsg.jpb.util.Stopwatch;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@SpringBootApplication
@EnableSpringConfigured
public class Application {

  private static final Logger LOG = LogManager.getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      System.out.println("Let's inspect the beans provided by Spring Boot:");
      String[] beanNames = ctx.getBeanDefinitionNames();
      Arrays.sort(beanNames);
      for (String beanName : beanNames) {
        System.out.println(beanName);
      }
    };
  }

  @Bean
  public CommandLineRunner cmdRunner(JpaDatabaseWriter writer) {
    return args -> {
      JpaDataGenerator jpaDataGenerator = new JpaDataGenerator(1, true);
      LOG.info("Beginning data generation...");
      Stopwatch stopwatch = new Stopwatch(true);
      jpaDataGenerator.generate();
      stopwatch.stop();
      LOG.info("Data generation took {} seconds", stopwatch.getDurationSeconds());
      stopwatch.start();
      writer.writeAll(jpaDataGenerator);
      stopwatch.stop();
      LOG.info(
          "Successfully wrote sample data to database, took {} milliseconds",
          stopwatch.getDurationMillis());
    };
  }
}
