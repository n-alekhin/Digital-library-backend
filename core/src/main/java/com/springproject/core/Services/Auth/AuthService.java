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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService  {

  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;

  public JwtResponse login(@NonNull JwtRequest authRequest) {
    Optional<User> optionalUser = userRepository.getByLogin(authRequest.getLogin());
    //  if (!optionalUser.isPresent())
//      throw new AuthException();
//    if(authRequest.getLogin() == null || authRequest.getPassword()==null){
//      throw new LoginException("123");
//    }
    UserDto userDto = UserMapperImpl.toUserDto(optionalUser.get());
    if (BCrypt.checkpw(authRequest.getPassword(), optionalUser.get().getPassword())) {

      final String accessToken = jwtProvider.generateAccessToken(userDto);
      final String refreshToken = jwtProvider.generateRefreshToken(userDto);
      Token token = optionalUser.get().getToken();
      token.setRefreshToken(refreshToken);
      userRepository.save(optionalUser.get());
      return new JwtResponse(userDto.getId() ,accessToken, refreshToken);
    } else {
//      throw new AuthException();
      return  null;
    }
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

