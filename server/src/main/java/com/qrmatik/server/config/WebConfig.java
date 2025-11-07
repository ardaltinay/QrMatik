package com.qrmatik.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableScheduling
public class WebConfig {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Value("${client.url:}")
    private String clientUrl;

    // Optional: allow wildcard subdomains in prod like https://*.example.com
    // Comma-separated list supported. Leave empty to fall back to clientUrl only.
    @Value("${client.allowed-origin-pattern:}")
    private String clientAllowedOriginPattern;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 1) Callback first: make sure the specific rule wins over generic /api/**
                // mapping
                registry.addMapping("/api/public/iyzico/callback").allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*").allowedOriginPatterns("*").allowCredentials(false);

                // 2) General CORS for application APIs (frontend <-> backend)
                var mapping = registry.addMapping("/api/**").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*").allowCredentials(true);

                // Prefer patterns when provided (supports wildcards with credentials)
                if (clientAllowedOriginPattern != null && !clientAllowedOriginPattern.isBlank()) {
                    String[] patterns = clientAllowedOriginPattern.split("\\s*,\\s*");
                    mapping.allowedOriginPatterns(patterns);
                } else if (clientUrl != null && !clientUrl.isBlank()) {
                    mapping.allowedOrigins(clientUrl);
                } else {
                    // Safer dev fallback: restrict to common local dev origins instead of allowing
                    // all
                    mapping.allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173");
                    // allow wildcard subdomains like http://{tenant}.localhost:5173
                    mapping.allowedOriginPatterns("http://*.localhost:5173", "http://*.localhost");
                }
                // Note: callback mapping already configured above
            }
        };
    }

    @Bean
    public WebMvcConfigurer staticResourceConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Resolve uploadDir to an absolute filesystem path, relative to server module
                // root if not absolute
                String absPath;
                try {
                    Path configured = Paths.get(uploadDir);
                    Path resolved;
                    if (configured.isAbsolute()) {
                        resolved = configured;
                    } else {
                        URI codeSrc = WebConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                        Path loc = Paths.get(codeSrc);
                        // if running from target/classes, go up to module root
                        if (loc.getFileName() != null && loc.getFileName().toString().equals("classes")) {
                            loc = loc.getParent();
                        }
                        if (loc.getFileName() != null && loc.getFileName().toString().equals("target")) {
                            loc = loc.getParent();
                        }
                        Path moduleRoot = loc;
                        resolved = moduleRoot.resolve(uploadDir);
                    }
                    absPath = resolved.toAbsolutePath().normalize().toString();
                } catch (Exception ex) {
                    absPath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();
                }
                String location = "file:" + (absPath.endsWith("/") ? absPath : absPath + "/");
                registry.addResourceHandler("/files/**").addResourceLocations(location).setCachePeriod(3600);
            }
        };
    }
}
