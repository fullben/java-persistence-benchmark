package de.uniba.dsg.jpb.server.auth;

import de.uniba.dsg.jpb.server.data.access.jpa.EmployeeRepository;
import de.uniba.dsg.jpb.server.data.model.jpa.EmployeeEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeUserDetailsService implements UserDetailsService {

  private final EmployeeRepository employeeRepository;

  @Autowired
  public EmployeeUserDetailsService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  public EmployeeUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    EmployeeEntity employee = employeeRepository.findByUsername(username);
    if (employee == null) {
      throw new UsernameNotFoundException("Unable to find user with name " + username);
    }
    return new EmployeeUserDetails(
        employee.getUsername(),
        employee.getPasswordHash(),
        employee.getSalt(),
        List.of(new SimpleGrantedAuthority(Role.TERMINAL_USER.prefixedName())));
  }
}
