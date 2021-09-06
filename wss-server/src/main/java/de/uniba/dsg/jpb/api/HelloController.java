package de.uniba.dsg.jpb.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * General Kenobi. You are a bold one :)
 *
 * @author Benedikt Full
 */
@RestController
public class HelloController {

  @GetMapping("/")
  public String index() {
    return "Hello there.";
  }
}