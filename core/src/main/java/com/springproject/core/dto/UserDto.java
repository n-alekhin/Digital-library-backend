package com.springproject.core.dto;

import com.springproject.core.dto.domain.Role;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
  @Schema(hidden = true)
  private Long id;
  private String login;
  private String password;
  @Schema(hidden = true)
  private Set<Role> roles;

}