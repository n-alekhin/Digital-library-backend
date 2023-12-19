package com.springproject.core.Services;

import com.springproject.core.model.dto.UserDto;

public interface UserService {
  Long createUser(UserDto userDto, int role);

}
