package com.springproject.core.Services;

import com.springproject.core.Repository.UserRepository;
import com.springproject.core.model.Entity.User;
import com.springproject.core.model.dto.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.getByLogin(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (user.getIsBanned()) {
      throw new UsernameNotFoundException("User is banned");
    }
    Set<Role> authorities = new HashSet<>();
    if (Objects.equals(user.getRole(), Role.SUPER_ADMIN.getAuthority())){
      authorities.add(Role.SUPER_ADMIN);
      authorities.add(Role.ADMIN);
      authorities.add(Role.USER);
    }
    if (Objects.equals(user.getRole(), Role.ADMIN.getAuthority())){
      authorities.add(Role.ADMIN);
      authorities.add(Role.USER);
    }
    if (Objects.equals(user.getRole(), Role.USER.getAuthority())){
      authorities.add(Role.USER);
    }

      return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
  }
}