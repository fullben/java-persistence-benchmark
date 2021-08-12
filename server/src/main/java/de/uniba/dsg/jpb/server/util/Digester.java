package de.uniba.dsg.jpb.server.util;

import static java.util.Objects.requireNonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Digester {

  private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
  private final Random random;

  public Digester() {
    random = new SecureRandom();
  }

  public String digest(String message, String salt) {
    requireNonNull(message);
    requireNonNull(salt);
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-512");
    } catch (NoSuchAlgorithmException e) {
      throw new MessageDigestException(e);
    }
    md.update(salt.getBytes(StandardCharsets.UTF_8));
    return toHex(md.digest(message.getBytes(StandardCharsets.UTF_8)));
  }

  public String randomSalt() {
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return toHex(salt);
  }

  private static String toHex(byte[] bytes) {
    requireNonNull(bytes);
    if (bytes.length == 0) {
      return "";
    }
    byte[] hexChars = new byte[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
      int v = bytes[i] & 0xFF;
      hexChars[i * 2] = HEX_ARRAY[v >>> 4];
      hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars, StandardCharsets.UTF_8);
  }
}
