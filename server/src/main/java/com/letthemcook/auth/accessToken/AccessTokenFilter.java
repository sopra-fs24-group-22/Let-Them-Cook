package com.letthemcook.auth.accessToken;

import com.letthemcook.auth.config.JwtHelper;
import com.letthemcook.user.User;
import com.letthemcook.user.UserRepository;
import com.letthemcook.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AccessTokenFilter extends OncePerRequestFilter {
  @Autowired
  private HandlerExceptionResolver handlerExceptionResolver;
  @Autowired
  private JwtHelper jwtHelper;
  @Autowired
  private UserService userService;
  @Autowired
  private UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    //Check if Token is present in request and validate
    try {
      Optional<String> accessToken = parseAccessToken(request);
      if (accessToken.isPresent() && jwtHelper.validateAccessToken(accessToken.get())) {
        String userId = jwtHelper.getUserIdFromAccessToken(accessToken.get());
        User user = userRepository.getById(Long.valueOf(userId));
        UsernamePasswordAuthenticationToken update = new UsernamePasswordAuthenticationToken(user, null);
        update.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(update);
      }

      filterChain.doFilter(request, response);
    } catch (Exception e) {
      handlerExceptionResolver.resolveException(request, response, null, e);
    }
  }

  private Optional<String> parseAccessToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      return Optional.of(authHeader.replace("Bearer ", ""));
    }
    return Optional.empty();
  }
}
