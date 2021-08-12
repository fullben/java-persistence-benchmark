package de.uniba.dsg.jpb.server.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class StringRandomTests {

  @Test
  public void nextStringHasExpectedLength() {
    int length = 100;
    StringRandom r = new StringRandom(length);
    for (int i = 0; i < 1_000_000; i++) {
      String next = r.nextString();
      assertEquals(length, next.length());
    }
  }

  @Test
  public void nextStringLengthRemainsInRange() {
    int min = 5;
    int max = 25;
    StringRandom r = new StringRandom(min, max);
    for (int i = 0; i < 1_000_000; i++) {
      String next = r.nextString();
      assertTrue(next.length() >= min && next.length() <= max);
    }
  }
}
