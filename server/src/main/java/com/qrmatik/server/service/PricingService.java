package com.qrmatik.server.service;

import com.qrmatik.server.config.PricingProperties.Props;
import com.qrmatik.server.dto.PricingDto;
import com.qrmatik.server.dto.PricingTierDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Central source of truth for pricing used by both public API and billing.
 * Values are sourced from configuration (pricing.*) with sensible defaults.
 */
@Service
public class PricingService {

    private final Props props;

    public PricingService(Props props) {
        this.props = props;
    }

    public PricingDto currentPricing() {
        return PricingDto.builder().currency(props.getCurrency()).note(props.getNote())
                .tiers(List.of(
                        PricingTierDto.builder().name("Ücretsiz").monthly(0).yearly(0)
                                .features(List.of("10 masa", "50 ürün", "QR menü ve temel sipariş",
                                        "Renk/Logo özelleştirme"))
                                .build(),
                        PricingTierDto.builder().name("Standart").monthly(props.getStandardMonthly())
                                .yearly(props.getStandardYearly())
                                .features(List.of("50 masa", "500 ürün", "Mutfak & Bar panoları",
                                        "Popüler ürünler ve raporlar", "Öncelikli e-posta desteği"))
                                .build(),
                        PricingTierDto.builder().name("Pro").monthly(props.getProMonthly()).yearly(props.getProYearly())
                                .features(List.of("Sınırsız masa ve ürün", "Gelişmiş raporlar",
                                        "İsteğe bağlı özel alan adı (CNAME)", "Gelişmiş stok kontrolü",
                                        "Öncelikli destek"))
                                .build()))
                .build();
    }

    public int priceFor(String plan, String period) {
        String p = plan == null ? "" : plan.trim().toUpperCase();
        String b = period == null ? "MONTHLY" : period.trim().toUpperCase();
        if ("STANDARD".equals(p)) {
            return "YEARLY".equals(b) ? props.getStandardYearly() : props.getStandardMonthly();
        }
        if ("PRO".equals(p)) {
            return "YEARLY".equals(b) ? props.getProYearly() : props.getProMonthly();
        }
        return 0;
    }
}
