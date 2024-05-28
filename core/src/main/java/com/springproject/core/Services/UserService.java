package com.springproject.core.Services;

import com.springproject.core.model.dto.UserDto;
import com.springproject.core.model.dto.UserDtoResponse;

public interface UserService {
  void createUser(UserDto userDto, int role);

  Long banAdmin(Long userId);
  Long ban(Long userId, Long IdAdmin);
  Long grantAdminRights(Long userId);
  Long revokeAdminRights(Long userId);
  UserDtoResponse getUser(Long userId);
  void changeNotificationPolicy(Long userId, boolean isSend);

}
