package com.springproject.core.Services;

import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.exceptions.InvalidAuthException;
import com.springproject.core.model.Entity.Token;
import com.springproject.core.model.Entity.User;
import com.springproject.core.Repository.UserRepository;
import com.springproject.core.model.dto.UserDto;
import com.springproject.core.model.dto.UserDtoResponse;
import com.springproject.core.model.dto.domain.JwtResponse;
import com.springproject.core.model.dto.domain.Role;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.annotations.NotFound;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final ModelMapper mapper;
  private final PasswordEncoder passwordEncoder;
  private final AuthService authService;

  public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, PasswordEncoder passwordEncoder, AuthService authService) {
    this.userRepository = userRepository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
      this.authService = authService;
  }

  public JwtResponse createUser(UserDto userDto, int role) {
    if(userRepository.getByLogin(userDto.getLogin()).isPresent()){
      throw new InvalidAuthException("Логин занят");
    }
    userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    User user = mapper.map(userDto, User.class);
    userDto.setId(userRepository.save(user).getId());

    Token token = new Token();
    token.setUser(user);
    user.setToken(token);

    if (role == 1 )
      user.setRole(Role.ADMIN.getAuthority());
    if (role == 0 )
      user.setRole(Role.USER.getAuthority());
    if (role == 2 )
      user.setRole(Role.SUPER_ADMIN.getAuthority());

    return authService.reg(userDto, user);
  }

  @Override
  public Long banAdmin(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    user.setIsBanned(true);
    return userRepository.save(user).getId();
  }

  @Override
  public Long ban(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    if (!Objects.equals(user.getRole(), Role.USER.getAuthority())) {
      throw new AccessDeniedException("У Пользователя нет разрешения на выполнение этого действия");
    }
    user.setIsBanned(true);
    return userRepository.save(user).getId();
  }

  @Override
  public Long grantAdminRights(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    if (Objects.equals(user.getRole(), Role.SUPER_ADMIN.getAuthority())) {
      throw new AccessDeniedException("У Пользователя нет разрешения на выполнение этого действия");
    }
    user.setRole(Role.ADMIN.getAuthority());
    return userRepository.save(user).getId();
  }

  @Override
  public Long revokeAdminRights(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    if (Objects.equals(user.getRole(), Role.SUPER_ADMIN.getAuthority())) {
      throw new AccessDeniedException("У Пользователя нет разрешения на выполнение этого действия");
    }
    user.setRole(Role.USER.getAuthority());
    return userRepository.save(user).getId();
  }

  @Override
  public UserDtoResponse getUser(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    return new UserDtoResponse(user.getName(), user.getRole());
  }

}
