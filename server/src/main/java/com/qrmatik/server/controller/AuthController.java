package com.qrmatik.server.controller;

import com.qrmatik.server.service.AuthService;
import com.qrmatik.server.service.TenantContext;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String tenant = TenantContext.getTenant();
        return authService.login(req.username(), req.password(), tenant).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Hatalı kullanıcı adı veya parola")));
    }
}
