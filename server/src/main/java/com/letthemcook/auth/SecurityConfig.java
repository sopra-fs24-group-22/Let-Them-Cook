package com.letthemcook.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
  private final UserAuthenticationProvider userAuthenticationProvider;
  private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;

  public SecurityConfig(UserAuthenticationProvider userAuthenticationProvider, UserAuthenticationEntryPoint userAuthenticationEntryPoint) {
    this.userAuthenticationProvider = userAuthenticationProvider;
    this.userAuthenticationEntryPoint = userAuthenticationEntryPoint;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .exceptionHandling().authenticationEntryPoint(userAuthenticationEntryPoint)
            .and()
            .addFilterBefore(new EmailPasswordAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
            .addFilterBefore(new JWTAuthFilter(userAuthenticationProvider), EmailPasswordAuthFilter.class)
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests(requests -> requests.antMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll().anyRequest().authenticated());

    return http.build();
  }

  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/auth/login", "/api/auth/register").permitAll() // Exclude login and token endpoints from authentication
            .anyRequest().authenticated();
  }
}
