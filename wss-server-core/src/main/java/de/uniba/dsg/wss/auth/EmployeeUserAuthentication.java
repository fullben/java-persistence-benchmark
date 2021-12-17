package de.uniba.dsg.wss.auth;

import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Facade for providing access to the current {@link Authentication} in a non-static way (by
 * default, this authentication can be accessed by calling {@code
 * SecurityContextHolder.getContext().getAuthentication()}.
 *
 * <p>Besides providing access to the current authentication, it also exposes methods for
 * conveniently accessing some properties and validating certain conditions.
 *
 * @author Benedikt Full
 */
@Component
public class EmployeeUserAuthentication {

  public EmployeeUserAuthentication() {}

  /** @return the current authentication or {@code null} if there is none */
  public Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  /**
   * @return the name of the current authentication or {@code null} if there is no authentication
   *     available
   */
  public String getName() {
    Authentication authentication = getAuthentication();
    if (authentication == null) {
      return null;
    }
    return authentication.getName();
  }

  /**
   * Returns whether the current authentication has the given authority (a role or privilege).
   *
   * @param authority a role or privilege name
   * @return {@code true} if the current authentication has the given authority, {@code false} in
   *     any other case
   */
  public boolean hasAuthority(String authority) {
    Authentication authentication = getAuthentication();
    if (authentication == null) {
      return false;
    }
    return authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals(authority));
  }

  /**
   * Returns whether the current authentication has the given role.
   *
   * <p>Note that the role name can be provided with or without the default Spring Security role
   * prefix {@code ROLE_}. Therefore, calling this method with a role name such as {@code ADMIN}
   * will always yield the same result as when calling the method with {@code ROLE_ADMIN}.
   *
   * @param role the role name
   * @return {@code true} if the current authentication has the given role, {@code false} otherwise
   */
  public boolean hasRole(String role) {
    if (!Roles.list().contains(role)) {
      throw new IllegalArgumentException("Unknown role: " + role);
    }
    return hasAuthority(Roles.prefixed(role));
  }

  /**
   * Returns whether the current authentication's name is equal to the given name.
   *
   * @param name the username of some user
   * @return {@code true} if the name of the current authentication and the given name match (even
   *     if both are {@code null}), {@code false} otherwise
   */
  public boolean hasName(String name) {
    return Objects.equals(getName(), name);
  }
}
