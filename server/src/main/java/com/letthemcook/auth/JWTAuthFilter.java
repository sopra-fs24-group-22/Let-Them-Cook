package com.letthemcook.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.http.HttpHeaders;

public class JWTAuthFilter extends OncePerRequestFilter {
  private final UserAuthenticationProvider provider;

  public JWTAuthFilter(UserAuthenticationProvider provider) {
    this.provider = provider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");

    if (header != null) {
      String[] authElements = header.split(" ");

      if (authElements.length == 2 && "Bearer".equals(authElements[0])) {
        try {
          SecurityContextHolder.getContext().setAuthentication(provider.validateToken(authElements[1]));
        } catch (RuntimeException e) {
          SecurityContextHolder.clearContext();
          throw e;
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
