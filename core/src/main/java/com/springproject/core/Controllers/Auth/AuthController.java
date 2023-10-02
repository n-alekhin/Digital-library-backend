package com.springproject.core.Controllers.Auth;

import com.springproject.core.Services.Auth.AuthService;
import com.springproject.core.dto.domain.JwtRequest;
import com.springproject.core.dto.domain.JwtResponse;
import com.springproject.core.dto.domain.RefreshJwtRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("login")
  public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) {
    final JwtResponse token = authService.login(authRequest);
    return ResponseEntity.ok(token);
  }

  @PostMapping("token")
  public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
    final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
    return ResponseEntity.ok(token);
  }

  @PostMapping("refresh")
  public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
    final JwtResponse token = authService.refresh(request.getRefreshToken());
    return ResponseEntity.ok(token);
  }

}