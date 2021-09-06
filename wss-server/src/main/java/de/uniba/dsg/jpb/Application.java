package de.uniba.dsg.jpb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

/**
 * The main class of the wholesale supplier server.
 *
 * @author Benedikt Full
 */
@SpringBootApplication
@EnableSpringConfigured
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
