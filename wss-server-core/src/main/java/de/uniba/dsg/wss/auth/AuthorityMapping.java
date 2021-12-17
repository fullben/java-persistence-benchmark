package de.uniba.dsg.wss.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Defines the hierarchy of user roles and mapping of these roles to privileges. The roles and
 * privileges used are the ones defined in {@link Roles} and {@link Privileges}, respectively.
 *
 * <p>This component is the authoritative instance for role and privilege mapping. Thus, it must be
 * consulted anytime information regarding authority mapping is required.
 *
 * @author Benedikt Full
 */
@Component
public class AuthorityMapping {

  private final Map<String, Set<String>> rolesToPrivileges;
  private final Map<String, Set<String>> rolesToRoles;

  public AuthorityMapping() {
    rolesToPrivileges = new HashMap<>();
    rolesToPrivileges.put(Roles.USER, Set.of(Privileges.READ_DATA_OWN));
    rolesToPrivileges.put(
        Roles.TERMINAL_USER,
        Set.of(Privileges.READ_DATA_ALL, Privileges.EXECUTE_BUSINESS_TRANSACTIONS_ALL));
    rolesToPrivileges.put(Roles.ADMIN, Set.of());
    if (rolesToPrivileges.size() != listRoles().size()) {
      throw new IllegalStateException(
          "Total number of roles and roles with mapped permissions do not match");
    }
    rolesToRoles = new HashMap<>();
    rolesToRoles.put(Roles.ADMIN, Set.of(Roles.TERMINAL_USER));
    rolesToRoles.put(Roles.TERMINAL_USER, Set.of(Roles.USER));
  }

  /**
   * Returns all authorities ({@link Roles} and {@link Privileges}) that become effective for a user
   * in the given role.
   *
   * <p>Note that all effective roles found will be prefixed with the {@link Roles#ROLE_PREFIX}.
   *
   * @param roles a set of role names, may be empty, but all actual values must correspond to one of
   *     the roles defined in {@link Roles}
   * @return a set of effective roles and privileges, may be empty
   * @see #getEffectiveAuthorities(String)
   */
  public Set<String> getEffectiveAuthorities(Collection<String> roles) {
    if (roles.isEmpty()) {
      return new HashSet<>(0);
    }
    return roles.stream()
        .flatMap(r -> getEffectiveAuthorities(r).stream())
        .collect(Collectors.toSet());
  }

  /**
   * Returns all authorities ({@link Roles} and {@link Privileges} that become effective for a user
   * that has the given role.
   *
   * <p>Note that all effective roles found will be prefixed with the {@link Roles#ROLE_PREFIX}.
   *
   * @param role one of the role names defined in {@link Roles}
   * @return a set of effective roles and privileges
   * @see #getEffectiveAuthorities(Collection)
   */
  public Set<String> getEffectiveAuthorities(String role) {
    Set<String> effectiveRoles = getEffectiveRoles(requireRole(role));
    Set<String> effectiveAuthorities =
        effectiveRoles.stream().map(AuthorityMapping::prefixedRole).collect(Collectors.toSet());
    effectiveRoles.forEach(
        r -> {
          Set<String> privileges = rolesToPrivileges.get(r);
          if (privileges != null) {
            effectiveAuthorities.addAll(privileges);
          }
        });
    return effectiveAuthorities;
  }

  /**
   * Returns all privileges assigned to the given role.
   *
   * <p>Note that this method <i>does not</i> return the <i>effective privileges</i> of the role,
   * but only those privileges, that have been assigned directly to the given role.
   *
   * @param role one of the role names defined in {@link Roles}
   * @return a set of privileges, can be empty
   */
  public Set<String> getRolePrivileges(String role) {
    Set<String> privileges = rolesToPrivileges.get(requireRole(role));
    if (privileges == null || privileges.isEmpty()) {
      return new HashSet<>(0);
    }
    return new HashSet<>(privileges);
  }

  /**
   * Returns a string defining the hierarchy of the user roles in the syntax required by Spring's
   * {@link org.springframework.security.access.hierarchicalroles.RoleHierarchy RoleHierarchy}.
   *
   * @return the role hierarchy definition
   */
  public String getRoleHierarchy() {
    return rolesToRoles.entrySet().stream()
        .map(
            entry -> {
              String owner = entry.getKey();
              StringBuilder builder = new StringBuilder();
              entry
                  .getValue()
                  .forEach(
                      child -> {
                        builder.append(owner);
                        builder.append(" > ");
                        builder.append(child);
                        builder.append("\n");
                      });
              return builder.toString();
            })
        .collect(Collectors.joining());
  }

  public List<String> listRoles() {
    return Roles.list();
  }

  public List<String> listPrivileges() {
    return Privileges.list();
  }

  private Set<String> getEffectiveRoles(String role) {
    Set<String> roles = new HashSet<>();
    roles.add(role);
    Set<String> children = rolesToRoles.get(role);
    if (children == null || children.isEmpty()) {
      return roles;
    } else {
      children.forEach(child -> roles.addAll(getEffectiveRoles(child)));
    }
    return roles;
  }

  private String requireRole(String role) {
    String plainRole = stripPrefix(role);
    if (!listRoles().contains(role)) {
      throw new IllegalArgumentException("Not a role: " + role);
    }
    return plainRole;
  }

  private static String prefixedRole(String role) {
    return Roles.prefixed(role);
  }

  private static String stripPrefix(String role) {
    return role.startsWith(Roles.ROLE_PREFIX) ? role.substring(Roles.ROLE_PREFIX.length()) : role;
  }
}
