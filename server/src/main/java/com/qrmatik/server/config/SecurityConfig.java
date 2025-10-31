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

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.authorizeHttpRequests(auth -> auth.requestMatchers("/files/**", "/api/auth/**", "/api/tenant/config")
        .permitAll().requestMatchers(HttpMethod.GET, "/api/menu/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/orders/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/orders/session/**", "/api/orders/*").permitAll()
    // Müşteri iptali için özel uç nokta: sessionId doğrulaması ile
    .requestMatchers(HttpMethod.POST, "/api/orders/*/cancel").permitAll()
    // QR image endpoint publicly accessible for customer tracking
    .requestMatchers(HttpMethod.GET, "/api/qr/image").permitAll()
    // Sipariş durum güncellemeleri sadece yetkili personel: admin/kitchen/bar
    .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasAnyRole("ADMIN", "KITCHEN", "BAR")
                // superadmin-only endpoints
                .requestMatchers("/api/tenants/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/users/**", "/api/qr/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/tables/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/tables/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/menu/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN").anyRequest().authenticated());
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
