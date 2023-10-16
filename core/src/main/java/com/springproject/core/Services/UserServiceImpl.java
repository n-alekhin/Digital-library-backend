package com.springproject.core.Services;

import com.springproject.core.Entity.Token;
import com.springproject.core.Entity.User;
import com.springproject.core.Repository.UserRepository;
import com.springproject.core.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final ModelMapper mapper;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
  }

  public Long createUser(UserDto userDto, int role) {
    // проверки
    userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    User user = mapper.map(userDto, User.class);
    userRepository.save(user);

    Token token = new Token();
    token.setUser(user);
    user.setToken(token);

    if (role == 1 )
      user.setRole("ADMIN");
    else
      user.setRole("USER");


    return userRepository.save(user).getId();
  }

}
