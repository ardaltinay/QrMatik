package com.qrmatik.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${app.jwt.secret:ZmFrZVNlY3JldEtleU1hZGUyNXdvcmRzZm9yZGVtby0=}")
  private String secret;

  @Value("${app.jwt.expMinutes:60}")
  private long expMinutes;

  private Key getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String username, String role, String tenant) {
    long now = System.currentTimeMillis();
    Date issuedAt = new Date(now);
    Date expiry = new Date(now + expMinutes * 60_000);
    return Jwts.builder()
        .setSubject(username)
        .addClaims(Map.of("role", role, "tenant", tenant))
        .setIssuedAt(issuedAt)
        .setExpiration(expiry)
        .signWith(getKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parseToken(String token) {
    return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
  }
}
