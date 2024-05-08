package com.springproject.core.model.dto;

import com.springproject.core.model.dto.domain.Role;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
  @Schema(hidden = true)
  private Long id;
  @Email(message = "Incorrect email")
  @NotBlank(message = "Email is mandatory")
  private String login;
  @NotBlank(message = "Name is mandatory")
  private String name;
  @NotBlank(message = "Password is mandatory")
  @Size(min = 4, max = 32, message = "Password must be between 4 and 32 characters long")
  private String password;
  @Schema(hidden = true)
  private Set<Role> roles;
  @NotNull(message = "Choose whether we can send you notifications")
  private Boolean isSendNotification;
}