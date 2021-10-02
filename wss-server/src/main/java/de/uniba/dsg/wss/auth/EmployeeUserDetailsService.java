package de.uniba.dsg.wss.auth;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Implementations of this service provide access to the user details of employees.
 *
 * @author Benedikt Full
 */
public abstract class EmployeeUserDetailsService implements UserDetailsService {

  public EmployeeUserDetailsService() {}

  @Override
  public abstract EmployeeUserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException;

  protected EmployeeUserDetails createWithDefaultRole(String username, String password) {
    return new EmployeeUserDetails(
        username, password, List.of(new SimpleGrantedAuthority(Role.TERMINAL_USER.prefixedName())));
  }
}
