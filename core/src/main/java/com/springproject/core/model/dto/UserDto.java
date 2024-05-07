package com.springproject.core.model.dto;

import com.springproject.core.model.dto.domain.Role;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
  @Schema(hidden = true)
  private Long id;
  @Email(message = "Incorrect email")
  private String login;
  @NotBlank(message = "Name is mandatory")
  private String name;
  @NotBlank(message = "Password is mandatory")
  private String password;
  @Schema(hidden = true)
  private Set<Role> roles;
  @NotNull(message = "IsSendNotification is mandatory")
  private Boolean isSendNotification;
}