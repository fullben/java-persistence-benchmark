package de.uniba.dsg.wss.data.gen.model;

import java.util.Collection;

public class Privilege extends Base {

  private String name;
  private Collection<Role> roles;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Collection<Role> getRoles() {
    return roles;
  }

  public void setRoles(Collection<Role> roles) {
    this.roles = roles;
  }
}
