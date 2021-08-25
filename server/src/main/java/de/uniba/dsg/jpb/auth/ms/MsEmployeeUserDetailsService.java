package de.uniba.dsg.jpb.auth.ms;

import de.uniba.dsg.jpb.auth.EmployeeUserDetails;
import de.uniba.dsg.jpb.auth.EmployeeUserDetailsService;
import de.uniba.dsg.jpb.data.access.ms.DataManager;
import de.uniba.dsg.jpb.data.access.ms.DataNotFoundException;
import de.uniba.dsg.jpb.data.model.ms.EmployeeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsEmployeeUserDetailsService extends EmployeeUserDetailsService {

  private final DataManager dataManager;

  @Autowired
  public MsEmployeeUserDetailsService(DataManager dataManager) {
    this.dataManager = dataManager;
  }

  @Override
  public EmployeeUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    EmployeeUserDetails userDetails =
        dataManager.read(
            (root) -> {
              try {
                EmployeeData employee = root.findEmployeeByUsername(username);
                return createWithDefaultRole(employee.getUsername(), employee.getPassword());
              } catch (DataNotFoundException e) {
                return null;
              }
            });
    if (userDetails == null) {
      throw new UsernameNotFoundException("Unable to find user with name " + username);
    }
    return userDetails;
  }
}
