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

  /**
   * Creates a new {@code EmployeeUserDetails} instance with the given parameters.
   *
   * <p>Implementations of this type of service may use this method in their implementation of
   * {@link #loadUserByUsername(String)} to create the user details object to be returned.
   *
   * @param username the unique username identifying an employee account
   * @param password the corresponding password
   * @param role one of the roles defined in {@link Roles}, can be prefixed
   * @return the newly created user details object
   * @throws IllegalArgumentException if the given role is not one of the roles defined in {@link
   *     Roles}
   */
  protected EmployeeUserDetails createUserDetails(String username, String password, String role) {
    return new EmployeeUserDetails(
        username, password, List.of(new SimpleGrantedAuthority(Roles.prefixed(role))));
  }
}
