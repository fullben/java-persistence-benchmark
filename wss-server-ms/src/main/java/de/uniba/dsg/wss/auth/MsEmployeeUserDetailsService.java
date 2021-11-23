package de.uniba.dsg.wss.auth;

import de.uniba.dsg.wss.data.model.ms.EmployeeData;
import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Provides access to the user details of employees when the server is launched in MS persistence
 * mode.
 *
 * @author Benedikt Full, Johannes Manner
 */
@Service
public class MsEmployeeUserDetailsService extends EmployeeUserDetailsService {

  private final MsDataRoot dataRoot;

  @Autowired
  public MsEmployeeUserDetailsService(MsDataRoot dataRoot){
    this.dataRoot = dataRoot;
  }

  @Override
  public EmployeeUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    EmployeeData employee = dataRoot.getEmployees().entrySet().parallelStream()
            .filter(e -> e.getValue().getUsername().equals(username))
            .findAny()
            .orElseThrow(
                    () -> new UsernameNotFoundException("Unable to find user with name " + username)).getValue();
    return createWithDefaultRole(employee.getUsername(), employee.getPassword());
  }
}
