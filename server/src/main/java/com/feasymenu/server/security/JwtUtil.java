package com.feasymenu.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    // Artık default secret yok; üretimde zorunlu. Base64 encode edilmiş 32+ byte
    // (256 bit) gerekli.
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expMinutes:60}")
    private long expMinutes;

    @Value("${app.jwt.refreshExpDays:7}")
    private long refreshExpDays;

    // İsteğe bağlı: issuer ve audience (yoksa eklenmez)
    @Value("${app.jwt.issuer:}")
    private String issuer;

    @Value("${app.jwt.audience:}")
    private String audience;

    private Key key;

    @PostConstruct
    void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret (app.jwt.secret) tanımlanmalı ve boş olamaz");
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret.trim());
        } catch (Exception e) {
            throw new IllegalStateException("JWT secret base64 decode edilemedi: " + e.getMessage());
        }
        if (keyBytes.length < 32) { // HS256 için önerilen minimum 256-bit
            throw new IllegalStateException("JWT secret en az 32 byte (256 bit) olmalı. Mevcut: " + keyBytes.length);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getKey() {
        return this.key;
    }

    public String generateToken(String username, String role, String tenant) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiry = new Date(now + expMinutes * 60_000);
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("role", role);
        claims.put("tenant", tenant);
        
        var builder = Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiry);
        if (issuer != null && !issuer.isBlank())
            builder.issuer(issuer.trim());
        if (audience != null && !audience.isBlank())
            builder.audience().add(audience.trim()).and();
        return builder.signWith(getKey()).compact();
    }

    public String generateRefreshToken(String username) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiry = new Date(now + refreshExpDays * 24 * 60 * 60_000);
        return Jwts.builder().subject(username).issuedAt(issuedAt).expiration(expiry).signWith(getKey()).compact();
    }

    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    public Claims parseToken(String token) {
        var parser = Jwts.parser().verifyWith((javax.crypto.SecretKey) getKey());
        if (issuer != null && !issuer.isBlank())
            parser.requireIssuer(issuer.trim());
        if (audience != null && !audience.isBlank())
            parser.requireAudience(audience.trim());
        return parser.build().parseSignedClaims(token).getPayload();
    }

    public long getExpMinutes() {
        return expMinutes;
    }

    public long getRefreshExpDays() {
        return refreshExpDays;
    }
}
