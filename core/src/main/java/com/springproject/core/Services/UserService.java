package com.springproject.core.Services;

import com.springproject.core.model.dto.UserDto;
import com.springproject.core.model.dto.UserDtoResponse;
import com.springproject.core.model.dto.domain.JwtResponse;

public interface UserService {
  JwtResponse createUser(UserDto userDto, int role);

  Long banAdmin(Long userId);
  Long ban(Long userId);
  Long grantAdminRights(Long userId);
  Long revokeAdminRights(Long userId);
  UserDtoResponse getUser(Long userId);

}
