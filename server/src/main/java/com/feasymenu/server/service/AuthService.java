package com.feasymenu.server.service;

import com.feasymenu.server.exception.AccountSuspendedException;
import com.feasymenu.server.model.RefreshTokenEntity;
import com.feasymenu.server.model.UserEntity;
import com.feasymenu.server.repository.RefreshTokenRepository;
import com.feasymenu.server.repository.UserRepository;
import com.feasymenu.server.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<Map<String, Object>> login(String username, String password, String tenant) {
        // Boş string gelirse null kabul et
        String tCode = (tenant == null || tenant.trim().isEmpty()) ? null : tenant;

        var opt = (tCode != null)
                ? userRepository.findTopByUsernameAndTenant_CodeOrderByCreatedTimeDesc(username, tCode)
                : userRepository.findTopByUsernameAndTenantIsNullOrderByCreatedTimeDesc(username);

        if (opt.isEmpty()) {
            System.out.println("[AuthService] Login failed: User not found with username: " + username + " (Tenant: "
                    + tCode + ")");
            return Optional.empty();
        }

        UserEntity u = opt.get();
        String hash = u.getPasswordHash();
        if (hash == null || !passwordEncoder.matches(password, hash)) {
            System.out.println("[AuthService] Login failed: Password mismatch for user: " + username);
            return Optional.empty();
        }

        System.out.println("[AuthService] Login successful for user: " + username + " with role: " + u.getRole());

        String tenantCode = (u.getTenant() != null ? u.getTenant().getCode() : null);
        String tenantId = (u.getTenant() != null ? u.getTenant().getId().toString() : null);
        if (u.getTenant() != null && !u.getTenant().isActive()) {
            throw new AccountSuspendedException("error.auth.accountSuspended");
        }
        String accessToken = jwtUtil.generateToken(u.getUsername(), u.getRole() != null ? u.getRole().name() : null,
                tenantCode, tenantId);

        // Generate Refresh Token
        String refreshToken = jwtUtil.generateRefreshToken(u.getUsername());
        saveRefreshToken(u.getUsername(), refreshToken);

        String roleStr = (u.getRole() != null ? u.getRole().name().toLowerCase() : "user");
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", u.getUsername());
        userMap.put("role", roleStr);
        userMap.put("tenantCode", tenantCode);

        if (u.getTenant() != null) {
            Map<String, Object> tenantMap = new HashMap<>();
            tenantMap.put("subscriptionPlan",
                    u.getTenant().getPlan() != null ? u.getTenant().getPlan().name() : "FREE");
            tenantMap.put("logoUrl", u.getTenant().getLogoUrl());
            tenantMap.put("primaryColor", u.getTenant().getPrimaryColor());
            userMap.put("tenant", tenantMap);
        }

        return Optional.of(Map.of("token", accessToken, "refreshToken", refreshToken, "user", userMap));
    }

    private void saveRefreshToken(String username, String token) {
        refreshTokenRepository.deleteByUsername(username); // One refresh token per user for simplicity
        var rt = RefreshTokenEntity.builder().username(username).token(token)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS)).build();
        refreshTokenRepository.save(rt);
    }

    public Optional<Map<String, String>> refresh(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .filter(rt -> !rt.isRevoked() && rt.getExpiryDate().isAfter(Instant.now())).map(rt -> {
                    var user = userRepository.findTopByUsernameOrderByCreatedTimeDesc(rt.getUsername()).orElseThrow();
                    String tenantCode = (user.getTenant() != null ? user.getTenant().getCode() : null);
                    String tenantId = (user.getTenant() != null ? user.getTenant().getId().toString() : null);
                    String newAccessToken = jwtUtil.generateToken(user.getUsername(), user.getRole().name(), tenantCode,
                            tenantId);
                    return Map.of("token", newAccessToken);
                });
    }

    public void logout(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }

    public void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("error.auth.passwordTooShort");
        }
    }
}
