package com.springproject.core.model.dto.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class JwtRequest {
  @Email
  private String login;
  @NotBlank
  private String password;

}