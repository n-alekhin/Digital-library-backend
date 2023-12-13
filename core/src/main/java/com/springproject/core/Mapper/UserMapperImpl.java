package com.springproject.core.Mapper;

import com.springproject.core.model.Entity.User;
import com.springproject.core.model.dto.UserDto;
import com.springproject.core.model.dto.domain.Role;
import java.util.HashSet;
import java.util.Objects;

public class UserMapperImpl {

  public static UserDto toUserDto(User user) {
    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setLogin(user.getLogin());
    userDto.setPassword(user.getPassword());

    userDto.setRoles(new HashSet<>());
    userDto.getRoles().add(Role.USER);
    if(Objects.equals(user.getRole(), "ADMIN"))
      userDto.getRoles().add(Role.ADMIN);
    return userDto;
  }
  public static User toUser(UserDto userDto) {
    User user = new User();
    user.setLogin(userDto.getLogin());
    user.setPassword(userDto.getPassword());
    return user;
  }

}
