package ru.nsu.fit.digitallibrary.services.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  @Value("${application.security.jwt.secret-key}")
  private String secretKey;
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    return claimsResolver.apply(extractAllClaims(token));
  }

  public String generateAccessToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails, jwtExpiration);
  }
  public String generateRefreshToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  public String generateToken(Map<String, Object> claims, UserDetails userDetails, long lifetime) {
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setSubject(userDetails.getUsername())
        .setExpiration(new Date(System.currentTimeMillis() + lifetime))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }
  public Boolean isTokenValid(String token, UserDetails userDetails) {
    return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private Key getSignInKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }
}
