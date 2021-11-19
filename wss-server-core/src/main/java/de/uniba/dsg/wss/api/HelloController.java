package de.uniba.dsg.wss.api;

import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<String> index() {
    return ResponseEntity.ok("Hello there.");
  }
}
