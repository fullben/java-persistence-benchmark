package de.uniba.dsg.jpb.server.auth;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class EmployeeUserDetails implements UserDetails {

  private final String username;
  private final String password;
  private final String salt;
  private final Collection<? extends GrantedAuthority> grantedAuthorities;

  public EmployeeUserDetails(
      String username,
      String password,
      String salt,
      Collection<? extends GrantedAuthority> grantedAuthorities) {
    this.username = username;
    this.password = password;
    this.salt = salt;
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

  public String getSalt() {
    return salt;
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
