package de.uniba.dsg.jpb.auth.jpa;

import de.uniba.dsg.jpb.auth.EmployeeUserDetails;
import de.uniba.dsg.jpb.auth.EmployeeUserDetailsService;
import de.uniba.dsg.jpb.data.access.jpa.EmployeeRepository;
import de.uniba.dsg.jpb.data.model.jpa.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
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
