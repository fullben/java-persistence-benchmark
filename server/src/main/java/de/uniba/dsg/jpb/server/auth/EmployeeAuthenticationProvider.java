package de.uniba.dsg.jpb.server.auth;

import de.uniba.dsg.jpb.server.util.Digester;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class EmployeeAuthenticationProvider implements AuthenticationProvider {

  private static final Logger LOG = LogManager.getLogger(EmployeeAuthenticationProvider.class);
  private final EmployeeUserDetailsService userDetailsService;
  private final Digester digester;

  public EmployeeAuthenticationProvider(EmployeeUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
    digester = new Digester();
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    if (authentication == null || !supports(authentication.getClass())) {
      return null;
    }
    String username = authentication.getName();
    Object credentials = authentication.getCredentials();
    if (credentials == null) {
      throw new BadCredentialsException("Missing password");
    }
    String password = credentials.toString();
    EmployeeUserDetails userDetails = userDetailsService.loadUserByUsername(username);
    String hashedPassword = digester.digest(password, userDetails.getSalt());
    if (userDetails.getPassword().equals(hashedPassword)) {
      LOG.debug("Authenticated employee \"{}\"", username);
      return new UsernamePasswordAuthenticationToken(
          username, hashedPassword, userDetails.getAuthorities());
    } else {
      throw new BadCredentialsException("Failed to authenticate user");
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    if (authentication == null) {
      return false;
    }
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
