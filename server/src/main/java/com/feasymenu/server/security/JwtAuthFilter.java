package com.feasymenu.server.security;

import com.feasymenu.server.repository.TenantRepository;
import com.feasymenu.server.service.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TenantRepository tenantRepository;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(JwtUtil jwtUtil, TenantRepository tenantRepository) {
        this.jwtUtil = jwtUtil;
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = null;
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            token = auth.substring(7);
        } else if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("fm_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && !token.isBlank()) {
            try {
                Claims claims = jwtUtil.parseToken(token);
                String username = claims.getSubject();
                Object roleObj = claims.get("role");
                String role = roleObj instanceof String ? (String) roleObj : String.valueOf(roleObj);
                Object tenantObj = claims.get("tenant");
                String tenant = tenantObj instanceof String ? (String) tenantObj : null;
                Object tenantIdObj = claims.get("tenantId");
                String tenantId = tenantIdObj instanceof String ? (String) tenantIdObj : null;

                if (tenant != null && !tenant.trim().isEmpty()) {
                    TenantContext.setTenant(tenant.trim());
                    if (tenantId != null) {
                        try {
                            UUID tid = UUID.fromString(tenantId);
                            TenantContext.setTenantId(tid);

                            // Force logout check: if not superadmin, check if tenant is active
                            if (!"SUPERADMIN".equalsIgnoreCase(role)) {
                                var tenantEntity = tenantRepository.findById(tid);
                                if (tenantEntity.isEmpty() || !tenantEntity.get().isActive()) {
                                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "error.auth.accountSuspended");
                                    return;
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                var authToken = new UsernamePasswordAuthenticationToken(username, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                // Debug seviyesinde logla; prod'da gürültüyü azaltmak için düşük seviye
                if (log.isDebugEnabled()) {
                    log.debug("Invalid JWT token: {}", e.getMessage());
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
