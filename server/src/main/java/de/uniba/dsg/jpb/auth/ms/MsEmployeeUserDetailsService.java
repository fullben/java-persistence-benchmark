package de.uniba.dsg.jpb.auth.ms;

import de.uniba.dsg.jpb.auth.EmployeeUserDetails;
import de.uniba.dsg.jpb.auth.EmployeeUserDetailsService;
import de.uniba.dsg.jpb.data.access.ms.DataRoot;
import de.uniba.dsg.jpb.data.access.ms.EmployeeRepository;
import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsEmployeeUserDetailsService extends EmployeeUserDetailsService {

  private final EmployeeRepository employeeRepository;

  @Autowired
  public MsEmployeeUserDetailsService(DataRoot dataRoot) {
    employeeRepository = dataRoot.employeeRepository();
  }

  @Override
  public EmployeeUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    EmployeeData employee = employeeRepository.findByUsername(username).orElse(null);
    if (employee == null) {
      throw new UsernameNotFoundException("Unable to find user with name " + username);
    }
    return createWithDefaultRole(employee.getUsername(), employee.getPassword());
  }
}
