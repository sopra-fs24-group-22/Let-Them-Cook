package com.letthemcook.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  @Autowired
  private JwtService jwtService;
  @Autowired
  private UserDetailsService userDetailsService;
  @Qualifier("handlerExceptionResolver")
  @Autowired
  private HandlerExceptionResolver resolver;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String authHeader = request.getHeader("Authorization");
      String jwt;
      String username;
      String path = request.getRequestURI();

      if(path.contains("auth") || authHeader == null) {
        filterChain.doFilter(request, response);
        return;
      }

      if (!authHeader.startsWith("Bearer ")) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header.");
      }

      jwt = authHeader.substring(7);
      username = jwtService.extractUsername(jwt);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {
          SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          securityContext.setAuthentication(authenticationToken);
          SecurityContextHolder.setContext(securityContext);
        }

      }
    } catch (Exception e) {
      resolver.resolveException(request, response, null, e);
    }

    filterChain.doFilter(request, response);
  }
}
