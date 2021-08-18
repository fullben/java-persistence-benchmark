package de.uniba.dsg.jpb.server.auth;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public abstract class EmployeeUserDetailsService implements UserDetailsService {

  EmployeeUserDetailsService() {}

  @Override
  public abstract EmployeeUserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException;
}
