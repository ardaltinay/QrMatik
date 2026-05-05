package com.feasymenu.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PricingProperties.Props.class)
public class PricingProperties {
    @Data
    @ConfigurationProperties(prefix = "pricing")
    public static class Props {
        private String currency = "USD";
        private String note = "Yıllık ödemede avantajlı fiyatlar sunulur.";
        private double standardMonthly = 14.99;
        private double standardYearly = 149.99;
        private double proMonthly = 29.99;
        private double proYearly = 299.99;
    }
}
