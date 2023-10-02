package com.springproject.core.Services;

import com.springproject.core.dto.UserDto;

public interface UserService {
  public Long createUser(UserDto userDto, int role);
}
