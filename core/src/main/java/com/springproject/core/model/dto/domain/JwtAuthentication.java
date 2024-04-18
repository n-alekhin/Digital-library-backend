package com.springproject.core.model.dto.domain;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Setter
public class JwtAuthentication implements Authentication {

  private boolean authenticated;

  private String username;
  private String firstName;
  private Set<Role> roles;
  private Long id;


  public void setAuthorities(Collection<? extends GrantedAuthority> a ){
    roles = a.stream()
            .map(authority -> (Role.valueOf(authority.getAuthority())))
            .collect(Collectors.toSet());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getAuthority()))
            .collect(Collectors.toList());
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getDetails() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return username;
  }

  @Override
  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    this.authenticated = isAuthenticated;
  }

  @Override
  public String getName() {
    return firstName;
  }

}

