package ru.nsu.fit.digitallibrary.services.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.fit.digitallibrary.dto.AuthenticationRequest;
import ru.nsu.fit.digitallibrary.dto.AuthenticationResponse;
import ru.nsu.fit.digitallibrary.dto.RegisterRequest;
import ru.nsu.fit.digitallibrary.model.Role;
import ru.nsu.fit.digitallibrary.model.Token;
import ru.nsu.fit.digitallibrary.model.User;
import ru.nsu.fit.digitallibrary.repositories.TokenRepository;
import ru.nsu.fit.digitallibrary.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final ModelMapper mapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final TokenRepository tokenRepository;
  public AuthenticationResponse register(RegisterRequest userDetailsDTO) {
    userDetailsDTO.setPassword(passwordEncoder.encode(userDetailsDTO.getPassword()));
    User user = mapper.map(userDetailsDTO, User.class);
    user.setRole(Role.USER);
    userRepository.save(user);
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(user, accessToken);
    return new AuthenticationResponse(accessToken, refreshToken);
  }

  public AuthenticationResponse authenticate(AuthenticationRequest userDetailsDTO) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            userDetailsDTO.getEmail(),
            userDetailsDTO.getPassword()
        )
    );
    User user = userRepository.findByEmail(userDetailsDTO.getEmail())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, accessToken);
    return new AuthenticationResponse(accessToken, refreshToken);
  }
  private void saveUserToken(User user, String jwtToken) {
    Token token = Token.builder()
        .user(user)
        .token(jwtToken)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      User user = userRepository.findByEmail(userEmail)
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
      if (jwtService.isTokenValid(refreshToken, user)) {
        String accessToken = jwtService.generateAccessToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
