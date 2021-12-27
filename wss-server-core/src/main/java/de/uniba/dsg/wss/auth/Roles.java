package de.uniba.dsg.wss.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * The roles which can be assigned to the users of the wholesale supplier application. A user may
 * have multiple roles. Roles are used to determine which resources can be accessed by a user
 *
 * <p><b>Note:</b> If the roles defined in this class are modified, the mappings defined in the
 * {@link AuthorityMapping} must be updated accordingly.
 *
 * @author Benedikt Full
 */
public final class Roles {

  /**
   * The default prefix for roles in the Spring Security context, see {@link #prefixed(String)} for
   * prefixing roles.
   */
  public static final String ROLE_PREFIX = "ROLE_";
  /** The most basic and restricted user role. */
  public static final String USER = "USER";
  /**
   * Role for employees that use the terminals at the warehouses of the company. Permits wide
   * resource access and allows execution of business transactions.
   */
  public static final String TERMINAL_USER = "TERMINAL_USER";
  /**
   * The administrator is the highest level of authority possible. This role should only be assigned
   * to individuals that actually manage or develop instances of this application.
   */
  public static final String ADMIN = "ADMIN";

  private Roles() {
    throw new AssertionError();
  }

  /**
   * Returns a version of the given role name that starts with the {@link #ROLE_PREFIX}.
   *
   * <p>If the given role already starts with the {@code ROLE_PREFIX} (ignoring any leading
   * whitespace), this method will simply return the given name, stripped of all leading and
   * trailing whitespace.
   *
   * <p>If the given role name does not start with the {@code ROLE_PREFIX}, this method will strip
   * the given name of all leading and trailing whitespace, prepend the prefix, and return the
   * resulting name.
   *
   * @param role the role name, must be neither {@code null} nor blank
   * @return the given role name, prefixed with the {@link #ROLE_PREFIX}
   * @throws IllegalArgumentException if the given role does not represent a role as determined by
   *     {@link #isRole(String)}
   * @see #unprefixed(String)
   */
  public static String prefixed(String role) {
    if (!isRole(role)) {
      throw new IllegalArgumentException("Not a role: " + role);
    }
    String strippedRole = role.strip();
    if (strippedRole.startsWith(ROLE_PREFIX)) {
      return strippedRole;
    }
    return ROLE_PREFIX + strippedRole;
  }

  /**
   * Returns a version of the given role name that does not start with the {@link #ROLE_PREFIX}.
   *
   * <p>If the given role already does not start with the {@code ROLE_PREFIX} (ignoring any leading
   * whitespace), this method will simply return the given name, stripped of all leading and
   * trailing whitespace.
   *
   * <p>If the given role name starts with the {@code ROLE_PREFIX}, this method will strip the given
   * name of all leading and trailing whitespace, remove the prefix, and return the resulting name.
   *
   * @param role the role name, must be one of the roles defined in this class, can be prefixed with
   *     the {@link #ROLE_PREFIX}
   * @return the given role name, without the {@link #ROLE_PREFIX}
   * @throws IllegalArgumentException if the given role does not represent a role as determined by
   *     {@link #isRole(String)}
   * @see #prefixed(String)
   */
  public static String unprefixed(String role) {
    if (!isRole(role)) {
      throw new IllegalArgumentException("Not a role: " + role);
    }
    return removePrefix(role);
  }

  /** @return a list of all roles */
  public static List<String> list() {
    List<String> roles = new ArrayList<>(3);
    roles.add(USER);
    roles.add(TERMINAL_USER);
    roles.add(ADMIN);
    return roles;
  }

  /**
   * Checks whether the given name identifies one of the roles defined by this class.
   *
   * <p>Note that this implementation performs a 'fuzzy' check: the given role name is allowed to
   * start with the {@link #ROLE_PREFIX} and can contain leading and trailing whitespace.
   *
   * @param role some string
   * @return {@code true} if the given string matches one of the roles defined in this class,
   *     ignoring the default role prefix and any leading and trailing whitespace characters, {@code
   *     false} otherwise
   */
  public static boolean isRole(String role) {
    return role != null && !role.isBlank() && list().contains(removePrefix(role));
  }

  private static String removePrefix(String role) {
    if (role == null) {
      return null;
    }
    role = role.strip();
    return role.startsWith(ROLE_PREFIX) ? role.substring(ROLE_PREFIX.length()) : role;
  }
}
