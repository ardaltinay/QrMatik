package com.qrmatik.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    // Artık default secret yok; üretimde zorunlu. Base64 encode edilmiş 32+ byte (256 bit) gerekli.
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expMinutes:60}")
    private long expMinutes;

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
        var builder = Jwts.builder().setSubject(username)
                .addClaims(Map.of("role", role, "tenant", tenant))
                .setIssuedAt(issuedAt)
                .setExpiration(expiry);
        if (issuer != null && !issuer.isBlank()) builder.setIssuer(issuer.trim());
        if (audience != null && !audience.isBlank()) builder.setAudience(audience.trim());
        return builder.signWith(getKey(), SignatureAlgorithm.HS256).compact();
    }

    public Claims parseToken(String token) {
        var parser = Jwts.parserBuilder().setSigningKey(getKey());
        if (issuer != null && !issuer.isBlank()) parser.requireIssuer(issuer.trim());
        if (audience != null && !audience.isBlank()) parser.requireAudience(audience.trim());
        return parser.build().parseClaimsJws(token).getBody();
    }
}
