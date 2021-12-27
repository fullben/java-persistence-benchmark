package de.uniba.dsg.wss.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * The privileges which can be assigned to the users of this application. Privileges are used to
 * determine which resources can be accessed by an authenticated user in a more granular manner than
 * {@link Roles} do.
 *
 * <p><b>Note:</b> If the privileges defined in this class are modified, the mappings defined in the
 * {@link AuthorityMapping} must be updated accordingly.
 *
 * @author Benedikt Full
 */
public final class Privileges {

  /** Permits owners to read all data relevant to their own user account. */
  public static final String READ_DATA_OWN = "READ_DATA_OWN";
  /** Permits owners to read all data managed by instances of this application. */
  public static final String READ_DATA_ALL = "READ_DATA_ALL";
  /** Permits owners to execute all business transactions of this application. */
  public static final String EXECUTE_BUSINESS_TRANSACTIONS_ALL =
      "EXECUTE_BUSINESS_TRANSACTIONS_ALL";

  private Privileges() {
    throw new AssertionError();
  }

  /** @return a list of all privileges */
  public static List<String> list() {
    List<String> privileges = new ArrayList<>(3);
    privileges.add(READ_DATA_OWN);
    privileges.add(READ_DATA_ALL);
    privileges.add(EXECUTE_BUSINESS_TRANSACTIONS_ALL);
    return privileges;
  }
}
