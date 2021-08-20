package de.uniba.dsg.jpb.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@SpringBootApplication
@EnableSpringConfigured
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
