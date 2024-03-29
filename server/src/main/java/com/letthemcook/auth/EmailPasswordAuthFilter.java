package com.letthemcook.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letthemcook.user.UserDTO;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EmailPasswordAuthFilter extends OncePerRequestFilter {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private final UserAuthenticationProvider provider;

  public EmailPasswordAuthFilter(UserAuthenticationProvider provider) {
    this.provider = provider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if("/api/auth/login".equals(request.getServletPath()) && HttpMethod.POST.matches(request.getMethod())){
      UserDTO userDTO = MAPPER.readValue(request.getInputStream(), UserDTO.class);

      try {
        SecurityContextHolder.getContext().setAuthentication(provider.validateCredentials(userDTO));
      } catch (RuntimeException e) {
        SecurityContextHolder.clearContext();
        throw e;
      }
    }

    filterChain.doFilter(request, response);
  }
}
