package com.springproject.core.Services.Auth;


import com.springproject.core.dto.domain.JwtAuthentication;
import com.springproject.core.dto.domain.Role;
import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

  public static JwtAuthentication generate(Claims claims) {
    final JwtAuthentication jwtInfoToken = new JwtAuthentication();
    jwtInfoToken.setRoles(getRoles(claims));
    jwtInfoToken.setFirstName(claims.get("firstName", String.class));
    jwtInfoToken.setUsername(claims.getSubject());
    jwtInfoToken.setId(claims.get("id", Long.class));
    return jwtInfoToken;
  }
  private static Set<Role> getRoles(Claims claims) {
    List<String> roles = (List<String>) claims.get("roles");
    return roles.stream().map(Role::valueOf).collect(Collectors.toSet());
  }
}