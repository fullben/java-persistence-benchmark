package de.uniba.dsg.wss.ms.auth;

import de.uniba.dsg.wss.auth.EmployeeUserDetails;
import de.uniba.dsg.wss.auth.EmployeeUserDetailsService;
import de.uniba.dsg.wss.data.model.ms.EmployeeData;
import org.jacis.store.JacisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Provides access to the user details of employees when the server is launched in MS persistence
 * mode.
 *
 * @author Benedikt Full
 */
@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsEmployeeUserDetailsService extends EmployeeUserDetailsService {

  private final JacisStore<String, EmployeeData> employeeStore;

  @Autowired
  public MsEmployeeUserDetailsService(JacisStore<String, EmployeeData> employeeStore) {
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
    return createWithDefaultRole(employee.getUsername(), employee.getPassword());
  }
}
