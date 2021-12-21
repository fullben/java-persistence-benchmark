package de.uniba.dsg.wss.auth;

import de.uniba.dsg.wss.data.model.EmployeeData;
import de.uniba.dsg.wss.data.model.MsDataRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Provides access to the user details of employees.
 *
 * @author Benedikt Full
 * @author Johannes Manner
 */
@Service
public class MsEmployeeUserDetailsService extends EmployeeUserDetailsService {

  private final MsDataRoot dataRoot;

  @Autowired
  public MsEmployeeUserDetailsService(MsDataRoot dataRoot) {
    this.dataRoot = dataRoot;
  }

  @Override
  public EmployeeUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    EmployeeData employee =
        dataRoot.getEmployees().entrySet().parallelStream()
            .filter(e -> e.getValue().getUsername().equals(username))
            .findAny()
            .orElseThrow(
                () -> new UsernameNotFoundException("Unable to find user with name " + username))
            .getValue();
    return createUserDetails(employee.getUsername(), employee.getPassword(), employee.getRole());
  }
}
