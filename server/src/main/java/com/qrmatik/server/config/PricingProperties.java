package com.qrmatik.server.config;

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
        private String currency = "TRY";
        private String note = "Yıllık ödemede avantajlı fiyatlar sunulur.";
        private int standardMonthly = 249;
        private int standardYearly = 2000;
        private int proMonthly = 549;
        private int proYearly = 5000;
    }
}
