package de.uniba.dsg.wss.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configures the security of this server. This configuration ensures that the entire API provided
 * by implementations of the {@link de.uniba.dsg.wss.api.ResourceController ResourceController} and
 * {@link de.uniba.dsg.wss.api.TransactionController TransactionController} is restricted to users
 * which can authenticate themselves with basic authentication and hold the role {@link Roles#USER}.
 * Note that the controller implementations may restrict access even further.
 *
 * @author Benedikt Full
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final EmployeeUserDetailsService userDetailsService;
  private final AuthorityMapping authorityMapping;

  @Autowired
  public SecurityConfiguration(
      EmployeeUserDetailsService userDetailsService, AuthorityMapping authorityMapping) {
    this.userDetailsService = userDetailsService;
    this.authorityMapping = authorityMapping;
  }

  @Override
  public void configure(AuthenticationManagerBuilder authBuilder) {
    authBuilder.authenticationProvider(daoAuthenticationProvider());
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .headers()
        .frameOptions()
        .disable()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/api/**")
        .hasRole(Roles.USER)
        .and()
        .authorizeRequests()
        .antMatchers("/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .httpBasic()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.NEVER);
  }

  @Bean
  public AuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(userDetailsService);
    return provider;
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy(authorityMapping.getRoleHierarchy());
    return roleHierarchy;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
