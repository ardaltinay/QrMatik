package com.qrmatik.server.config;

import com.iyzipay.Options;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IyzicoOptionsConfig.IyzicoProperties.class)
public class IyzicoOptionsConfig {

    @Bean
    public Options iyzicoOptions(IyzicoProperties props) {
        Options options = new Options();
        options.setApiKey(props.getApiKey());
        options.setSecretKey(props.getSecretKey());
        options.setBaseUrl(props.getBaseUrl());
        return options;
    }

    @Data
    @ConfigurationProperties(prefix = "iyzico")
    public static class IyzicoProperties {
        /** API key from iyzico dashboard */
        private String apiKey = "";
        /** Secret key from iyzico dashboard */
        private String secretKey = "";
        /** Base URL, use https://sandbox-api.iyzipay.com for sandbox */
        private String baseUrl = "https://sandbox-api.iyzipay.com";
        /** Enable/disable payments feature without removing code */
        private boolean enabled = false;
        /** Public callback base URL of this app (e.g., https://app.yourdomain.com) */
        private String callbackBaseUrl = "";
    }
}
