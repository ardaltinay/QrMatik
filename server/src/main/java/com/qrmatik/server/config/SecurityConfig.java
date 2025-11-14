package com.qrmatik.server.config;

import com.qrmatik.server.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final TenantFilter tenantFilter;

    // Removed custom client origin wiring for callback per user's request

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, TenantFilter tenantFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.tenantFilter = tenantFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        // Keep default CORS handling; WebMvcConfigurer governs app CORS. Callback
        // returns 302 redirect, no CORS needed.
        http.cors(c -> {
        });
        // Allow framing from same origin (needed for certain PSP callbacks/embeds)
        http.headers(h -> h.frameOptions(f -> f.sameOrigin()));
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                // Allow CORS preflight requests universally
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/files/**", "/api/auth/**", "/api/tenant/config", "/api/public/**").permitAll()
                // Explicitly permit the public checkout HTML endpoint (GET) to avoid any
                // pattern-matching surprises in complex matcher setups or additional
                // security filters that may treat certain anonymous GETs specially.
                .requestMatchers(HttpMethod.GET, "/api/public/checkout/html").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/menu/**").permitAll()
                // Stok API sadece admin (PRO kontrolu controller icinde ayrica yapiliyor)
                .requestMatchers(HttpMethod.GET, "/api/stock/**").hasRole("ADMIN")
                // Customer flows (anonymous allowed)
                .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders/*/cancel").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders/*/request-bill").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/orders/session/**", "/api/orders/*").permitAll()
                // Staff-only POSTs under /api/orders (e.g., close-table)
                .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyRole("ADMIN", "CASHIER")
                // QR image endpoint publicly accessible for customer tracking
                .requestMatchers(HttpMethod.GET, "/api/qr/image").permitAll()
                // Sipariş durum güncellemeleri sadece yetkili personel:
                // admin/kitchen/bar/cashier
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/status")
                .hasAnyRole("ADMIN", "KITCHEN", "BAR", "CASHIER")
                // superadmin-only endpoints
                .requestMatchers("/api/tenants/**").hasRole("SUPERADMIN")
                // admin area: ADMIN only
                .requestMatchers("/api/users/**", "/api/qr/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/tables/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/tables/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/menu/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN").anyRequest().authenticated());
        // Resolve tenant context early (from Host/X-Tenant/path) before JWT processing
        http.addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Removed explicit CorsConfigurationSource per user's request; relying on
    // existing WebMvcConfigurer.

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
