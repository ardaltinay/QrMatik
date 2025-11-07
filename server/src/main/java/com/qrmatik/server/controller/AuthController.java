package com.qrmatik.server.controller;

import com.qrmatik.server.security.LoginRateLimiter;
import com.qrmatik.server.service.AuthService;
import com.qrmatik.server.service.TenantContext;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiter rateLimiter;

    public AuthController(AuthService authService, LoginRateLimiter rateLimiter) {
        this.authService = authService;
        this.rateLimiter = rateLimiter;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String tenant = TenantContext.getTenant();
        String ip = "unknown";
        try {
            ip = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                    .flatMap(a -> {
                        if (a instanceof ServletRequestAttributes s) {
                            return Optional.ofNullable(s.getRequest().getRemoteAddr());
                        }
                        return Optional.empty();
                    }).orElse("unknown");
        } catch (Exception ignored) {
        }
        String key = (tenant == null ? "_" : tenant) + "|" + req.username().toLowerCase() + "|" + ip;
        if (rateLimiter.isBlocked(key)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Çok fazla başarısız deneme. Lütfen daha sonra tekrar deneyin."));
        }
        return authService.login(req.username(), req.password(), tenant).map(r -> {
            rateLimiter.onSuccess(key);
            return ResponseEntity.ok(r);
        }).orElseGet(() -> {
            rateLimiter.onFailure(key);
            return ResponseEntity.status(401).body(Map.of("error", "Hatalı kullanıcı adı veya parola"));
        });
    }
}
