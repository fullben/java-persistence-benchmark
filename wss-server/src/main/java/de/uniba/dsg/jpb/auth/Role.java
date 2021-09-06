package de.uniba.dsg.jpb.auth;

/**
 * The roles which can be assigned to the users of this server.
 *
 * @author Benedikt Full
 */
public enum Role {
  TERMINAL_USER;

  public String prefixedName() {
    return "ROLE_" + name();
  }

  public String simpleName() {
    return name();
  }
}
