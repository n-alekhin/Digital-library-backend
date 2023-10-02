package com.springproject.core.dto;

import com.springproject.core.dto.domain.Role;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

  private Long id;
  private String login;
  private String password;
  private Set<Role> roles;

}