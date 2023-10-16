package com.springproject.core.Services.Auth;

import com.springproject.core.Entity.Token;
import com.springproject.core.Entity.User;
import com.springproject.core.Mapper.UserMapperImpl;
import com.springproject.core.Repository.UserRepository;
import com.springproject.core.dto.UserDto;
import com.springproject.core.dto.domain.JwtAuthentication;
import com.springproject.core.dto.domain.JwtRequest;
import com.springproject.core.dto.domain.JwtResponse;
import io.jsonwebtoken.Claims;
import java.sql.Timestamp;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService  {

  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;
  private final ModelMapper mapper;
  private final AuthenticationManager authenticationManager;
  public JwtResponse login(@NonNull JwtRequest authRequest) {
    //провеки
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            authRequest.getLogin(),
            authRequest.getPassword()
        )
    );
    User user = userRepository.getByLogin(authRequest.getLogin())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    UserDto userDto = mapper.map(user, UserDto.class);
    String accessToken = jwtProvider.generateAccessToken(userDto);
    String refreshToken = jwtProvider.generateRefreshToken(userDto);
    //revokeAllUserTokens(user);
      Token token = user.getToken();
      token.setRefreshToken(refreshToken);
      userRepository.save(user);
      return new JwtResponse(userDto.getId() ,accessToken, refreshToken);
  }

  public JwtResponse getAccessToken(@NonNull String refreshToken){
    if (jwtProvider.validateRefreshToken(refreshToken)) {
      final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
      final String login = claims.getSubject();

      Optional<User> optionalUser = userRepository.getByLogin(login);
//      if (!optionalUser.isPresent())
//        throw new AuthException();
      final String saveRefreshToken = optionalUser.get().getToken().getRefreshToken();
      if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
        final UserDto userDto = UserMapperImpl.toUserDto(optionalUser.get());
        final String accessToken = jwtProvider.generateAccessToken(userDto);
        return new JwtResponse(null, accessToken, null);
      }
    }
    return new JwtResponse(null, null, null);
  }


  public JwtResponse refresh(@NonNull String refreshToken) {
    if (jwtProvider.validateRefreshToken(refreshToken)) {
      final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
      final String login = claims.getSubject();
      Optional<User> optionalUser = userRepository.getByLogin(login);
//      if (!optionalUser.isPresent())
//        throw new AuthException();
      String saveRefreshToken = optionalUser.get().getToken().getRefreshToken();
      if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
        final UserDto userDto = UserMapperImpl.toUserDto(optionalUser.get());
        final String accessToken = jwtProvider.generateAccessToken(userDto);
        final String newRefreshToken = jwtProvider.generateRefreshToken(userDto);
        Token token = optionalUser.get().getToken();

        token.setRefreshToken(newRefreshToken);
        userRepository.save(optionalUser.get());
        return new JwtResponse(null, accessToken, newRefreshToken);
      }
    }
//    throw new LoginException("123");
    return  null;
  }

  public JwtAuthentication getAuthInfo() {
    return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
  }

}

