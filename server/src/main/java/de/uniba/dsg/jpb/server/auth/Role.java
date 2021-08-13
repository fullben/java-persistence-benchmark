package de.uniba.dsg.jpb.server.auth;

public enum Role {
  TERMINAL_USER;

  public String prefixedName() {
    return "ROLE_" + name();
  }

  public String simpleName() {
    return name();
  }
}
