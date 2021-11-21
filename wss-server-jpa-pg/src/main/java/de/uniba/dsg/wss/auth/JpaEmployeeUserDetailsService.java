package de.uniba.dsg.wss.auth;

import de.uniba.dsg.wss.data.access.EmployeeRepository;
import de.uniba.dsg.wss.data.model.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Provides access to the user details of employees when the server is launched in JPA persistence
 * mode.
 *
 * @author Benedikt Full
 */
@Service
public class JpaEmployeeUserDetailsService extends EmployeeUserDetailsService {

  private final EmployeeRepository employeeRepository;

  @Autowired
  public JpaEmployeeUserDetailsService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  public EmployeeUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    EmployeeEntity employee = employeeRepository.findByUsername(username).orElse(null);
    if (employee == null) {
      throw new UsernameNotFoundException("Unable to find user with name " + username);
    }
    return createWithDefaultRole(employee.getUsername(), employee.getPassword());
  }
}
