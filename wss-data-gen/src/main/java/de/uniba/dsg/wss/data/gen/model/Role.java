package de.uniba.dsg.wss.data.gen.model;

import java.util.Collection;

public class Role {

  private String name;
  private Collection<Privilege> privileges;
  private Collection<Employee> users;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Collection<Privilege> getPrivileges() {
    return privileges;
  }

  public void setPrivileges(Collection<Privilege> privileges) {
    this.privileges = privileges;
  }

  public Collection<Employee> getUsers() {
    return users;
  }

  public void setUsers(Collection<Employee> users) {
    this.users = users;
  }
}
