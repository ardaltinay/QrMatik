package com.feasymenu.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lemonsqueezy")
@Data
public class LemonSqueezyProperties {
    private String apiKey;
    private String webhookSecret;
    private String storeId;
    private Variant variant = new Variant();

    @Data
    public static class Variant {
        private String standardMonthly;
        private String standardYearly;
        private String proMonthly;
        private String proYearly;
    }
}
