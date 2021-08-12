package de.uniba.dsg.jpb.server.auth;

public final class Roles {

  public static final String PREFIX = "ROLE_";
  public static final String TERMINAL_USER = "TERMINAL";

  private Roles() {
    throw new AssertionError();
  }

  public static String prefixed(String roleName) {
    return PREFIX + roleName;
  }
}
