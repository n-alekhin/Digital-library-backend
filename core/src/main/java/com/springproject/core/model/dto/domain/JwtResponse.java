package com.springproject.core.model.dto.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponse {

  @Schema(hidden = true)
  private final String type = "Bearer";
  @Schema(hidden = true)
  private final Long id;
  private String accessToken;
  private String refreshToken;
  private String role;
  private String login;
  private Boolean isSendNotification;
}