package com.springproject.core.Services.Auth;


import com.springproject.core.Repository.UserRepository;
import com.springproject.core.model.dto.domain.JwtAuthentication;
import com.springproject.core.model.dto.domain.Role;
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
    jwtInfoToken.setFirstName(claims.get("firstName", String.class));
    jwtInfoToken.setUsername(claims.getSubject());
    jwtInfoToken.setId(claims.get("id", Long.class));
    return jwtInfoToken;
  }
}