package com.feasymenu.server.controller;

import com.feasymenu.server.security.JwtUtil;
import com.feasymenu.server.security.LoginRateLimiter;
import com.feasymenu.server.service.AuthService;
import com.feasymenu.server.service.TenantContext;
import com.feasymenu.server.service.TenantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiter rateLimiter;
    private final JwtUtil jwtUtil;
    private final TenantService tenantService;

    public AuthController(AuthService authService, LoginRateLimiter rateLimiter, JwtUtil jwtUtil,
            TenantService tenantService) {
        this.authService = authService;
        this.rateLimiter = rateLimiter;
        this.jwtUtil = jwtUtil;
        this.tenantService = tenantService;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request,
            HttpServletResponse response) {
        String tenant = TenantContext.getTenant();
        String ip = "unknown";
        try {
            ip = Optional.ofNullable(RequestContextHolder.getRequestAttributes()).flatMap(a -> {
                if (a instanceof ServletRequestAttributes s) {
                    return Optional.ofNullable(s.getRequest().getRemoteAddr());
                }
                return Optional.empty();
            }).orElse("unknown");
        } catch (Exception ignored) {
        }
        String key = (tenant == null ? "_" : tenant) + "|" + req.username().toLowerCase() + "|" + ip;
        boolean isBlocked = rateLimiter.isBlocked(key);

        return authService.login(req.username(), req.password(), tenant).map(r -> {
            rateLimiter.onSuccess(key);

            String token = (String) r.get("token");
            String refreshToken = (String) r.get("refreshToken");

            String origin = request.getHeader("Origin");
            String forwardedProto = request.getHeader("X-Forwarded-Proto");

            boolean isSecure = (origin != null && origin.startsWith("https"))
                    || "https".equalsIgnoreCase(forwardedProto);

            var cookie = ResponseCookie.from("qm_token", token).httpOnly(true).secure(isSecure).path("/")
                    .maxAge(jwtUtil.getExpMinutes() * 60).sameSite("Strict").build();

            var refreshCookie = ResponseCookie.from("qm_refresh_token", refreshToken).httpOnly(true).secure(isSecure)
                    .path("/").maxAge(jwtUtil.getRefreshExpDays() * 24 * 3600).sameSite("Strict").build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.ok(Map.of("user", r.get("user")));
        }).orElseGet(() -> {
            rateLimiter.onFailure(key);
            if (isBlocked) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of("error", "Çok fazla başarısız deneme. Lütfen 10 dakika sonra tekrar deneyin."));
            }
            return ResponseEntity.status(401).body(Map.of("error", "Hatalı kullanıcı adı veya parola"));
        });
    }

    public record RefreshRequest() {
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "qm_refresh_token", required = false) String refreshToken,
            HttpServletRequest request, HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token bulunamadı"));
        }
        return authService.refresh(refreshToken).map(r -> {
            String token = r.get("token");

            String origin = request.getHeader("Origin");
            String forwardedProto = request.getHeader("X-Forwarded-Proto");
            boolean isSecure = (origin != null && origin.startsWith("https"))
                    || "https".equalsIgnoreCase(forwardedProto);

            var cookie = ResponseCookie.from("qm_token", token).httpOnly(true).secure(isSecure).path("/")
                    .maxAge(jwtUtil.getExpMinutes() * 60).sameSite("Strict").build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok(Map.of("message", "Token yenilendi"));
        }).orElse(ResponseEntity.status(401).body(Map.of("error", "Geçersiz refresh token")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            authService.logout(auth.getName());
        }

        String origin = request.getHeader("Origin");
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        boolean isSecure = (origin != null && origin.startsWith("https")) || "https".equalsIgnoreCase(forwardedProto);

        var cookie = ResponseCookie.from("qm_token", "").httpOnly(true).secure(isSecure).path("/").maxAge(0).build();

        var refreshCookie = ResponseCookie.from("qm_refresh_token", "").httpOnly(true).secure(isSecure).path("/")
                .maxAge(0).build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Başarıyla çıkış yapıldı"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = auth.getName();
        String role = auth.getAuthorities().stream().map(a -> a.getAuthority().replace("ROLE_", "").toLowerCase())
                .findFirst().orElse("user");

        String tenantCode = TenantContext.getTenant();

        Map<String, Object> resp = new HashMap<>();
        resp.put("username", username);
        resp.put("role", role);
        resp.put("tenantCode", tenantCode);

        if (tenantCode != null) {
            try {
                var config = tenantService.getConfig(tenantCode, null);
                Map<String, Object> tenantMap = new HashMap<>();
                tenantMap.put("subscriptionPlan", config.get("plan"));
                tenantMap.put("logoUrl", config.get("logoUrl"));
                tenantMap.put("primaryColor", config.get("primaryColor"));
                resp.put("tenant", tenantMap);
            } catch (Exception e) {
                // Ignore if tenant config not found
            }
        }

        return ResponseEntity.ok(resp);
    }
}
