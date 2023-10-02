package com.springproject.core.Controllers;

import com.springproject.core.Services.UserService;
import com.springproject.core.dto.UserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/reg")
  public Long create(@RequestBody UserDto userDto) {
    return userService.createUser(userDto, 0);
  }

  @PostMapping("/regAdmin")
  public Long createAdmin(@RequestBody UserDto userDto) {
    return userService.createUser(userDto, 1);
  }
}
