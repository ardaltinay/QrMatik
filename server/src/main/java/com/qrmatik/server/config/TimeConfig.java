package com.qrmatik.server.config;

import java.time.Clock;
import java.time.ZoneId;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TimeConfig.AppProps.class)
public class TimeConfig {

    @Bean
    public ZoneId appZoneId(AppProps props) {
        return ZoneId.of(props.getTimeZone());
    }

    @Bean
    public Clock appClock(ZoneId appZoneId) {
        return Clock.system(appZoneId);
    }

    @Data
    @ConfigurationProperties(prefix = "app")
    public static class AppProps {
        // Default app-local timezone (Turkey)
        private String timeZone = "Europe/Istanbul";
    }
}
