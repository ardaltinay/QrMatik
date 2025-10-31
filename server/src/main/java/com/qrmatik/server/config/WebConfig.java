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

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**").allowedOrigins("http://localhost:5173", "http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public WebMvcConfigurer staticResourceConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Resolve uploadDir to an absolute filesystem path, relative to server module root if not absolute
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
