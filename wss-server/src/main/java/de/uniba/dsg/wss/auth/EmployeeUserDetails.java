package de.uniba.dsg.wss.auth;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Represents the user details of an employee.
 *
 * @author Benedikt Full
 */
public class EmployeeUserDetails implements UserDetails {

  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> grantedAuthorities;

  public EmployeeUserDetails(
      String username, String password, Collection<? extends GrantedAuthority> grantedAuthorities) {
    this.username = username;
    this.password = password;
    this.grantedAuthorities = grantedAuthorities;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return grantedAuthorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
