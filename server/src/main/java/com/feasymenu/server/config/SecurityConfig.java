package com.feasymenu.server.config;

import com.feasymenu.server.security.JwtAuthFilter;
import com.feasymenu.server.security.RateLimitFilter;
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
    private final RateLimitFilter rateLimitFilter;

    // Removed custom client origin wiring for callback per user's request

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, TenantFilter tenantFilter, RateLimitFilter rateLimitFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.tenantFilter = tenantFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(request -> {
            var cb = new org.springframework.web.cors.CorsConfiguration();
            cb.setAllowedOriginPatterns(java.util.List.of("http://localhost:3000", "http://*.localhost:3000", "http://*.localhost", "http://127.0.0.1:3000", "http://*.127.0.0.1:3000", "https://feasymenu.com", "https://*.feasymenu.com"));
            cb.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            cb.setAllowedHeaders(java.util.List.of("*"));
            cb.setAllowCredentials(true);
            return cb;
        }));
        // Allow framing from same origin (needed for certain PSP callbacks/embeds)
        http.headers(h -> h.frameOptions(f -> f.sameOrigin()));
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**", "/auth/**").permitAll()
                .requestMatchers("/files/**", "/api/tenant/config", "/api/public/**", "/ws/**", "/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/webhooks/lemonsqueezy").permitAll() // Lemon Squeezy Webhook
                .requestMatchers(HttpMethod.GET, "/api/menu/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/stock/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders/*/cancel").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders/*/request-bill").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders/call-waiter").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/orders/session/**", "/api/orders/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyRole("ADMIN", "CASHIER", "SALOON")
                .requestMatchers(HttpMethod.GET, "/api/qr/image").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/orders").hasAnyRole("ADMIN", "KITCHEN", "BAR", "CASHIER", "SALOON")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/status")
                .hasAnyRole("ADMIN", "KITCHEN", "BAR", "CASHIER", "SALOON")
                .requestMatchers("/api/tenants/**", "/api/admin/blog/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/users/**", "/api/qr/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/tables/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/tables/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/menu/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                .anyRequest().authenticated());
        // Filter Order: RateLimit -> Tenant -> JwtAuth -> UsernamePasswordAuthentication
        // By adding each before the same standard filter, the last one added becomes the first to run.
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
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
