package com.springproject.core.model.dto.domain;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class JwtRequest {

  private String login;
  private String password;

}