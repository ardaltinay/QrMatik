package com.qrmatik.server.service;

import com.qrmatik.server.model.UserEntity;
import com.qrmatik.server.repository.UserRepository;
import com.qrmatik.server.security.JwtUtil;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<Map<String, Object>> login(String username, String password, String tenant) {
    var opt = (tenant != null)
        ? userRepository.findTopByUsernameAndTenant_CodeOrderByCreatedTimeDesc(username, tenant)
        : userRepository.findTopByUsernameOrderByCreatedTimeDesc(username);
        if (opt.isEmpty())
            return Optional.empty();
        UserEntity u = opt.get();
        String hash = u.getPasswordHash();
        if (hash == null || !passwordEncoder.matches(password, hash)) {
            return Optional.empty();
        }
        String tenantCode = (u.getTenant() != null ? u.getTenant().getCode() : null);
        String token = jwtUtil.generateToken(u.getUsername(), u.getRole() != null ? u.getRole().name() : null, tenantCode);
        String roleStr = (u.getRole() != null ? u.getRole().name().toLowerCase() : null);
        return Optional.of(Map.of("token", token, "user", Map.of("username", u.getUsername(), "role", roleStr)));
    }
}
