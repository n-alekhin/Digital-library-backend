package com.springproject.core.model.dto.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
  SUPER_ADMIN("SUPER_ADMIN"),
  ADMIN("ADMIN"),
  USER("USER");

  private final String vale;

  @Override
  public String getAuthority() {
    return vale;
  }

}