package de.uniba.dsg.wss.auth;

import de.uniba.dsg.wss.data.model.EmployeeData;
import org.jacis.store.JacisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Provides access to the user details of employees when the server is launched in MS persistence
 * mode.
 *
 * @author Benedikt Full
 */
@Service
public class MsEmployeeUserDetailsService extends EmployeeUserDetailsService {

  private final JacisStore<String, EmployeeData> employeeStore;

  @Autowired
  public MsEmployeeUserDetailsService(
      AuthorityMapping authorityMapping, JacisStore<String, EmployeeData> employeeStore) {
    super(authorityMapping);
    this.employeeStore = employeeStore;
  }

  @Override
  public EmployeeUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    EmployeeData employee =
        employeeStore
            .streamReadOnly(e -> e.getUsername().equals(username))
            .parallel()
            .findAny()
            .orElseThrow(
                () -> new UsernameNotFoundException("Unable to find user with name " + username));
    return createUserDetails(employee.getUsername(), employee.getPassword(), employee.getRole());
  }
}
