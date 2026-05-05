package com.feasymenu.server.service;

import com.feasymenu.server.config.LemonSqueezyProperties;
import com.feasymenu.server.config.PricingProperties.Props;
import com.feasymenu.server.dto.PricingDto;
import com.feasymenu.server.dto.PricingTierDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Central source of truth for pricing used by both public API and billing.
 * Values are sourced from configuration (pricing.*) with sensible defaults.
 */
@Service
public class PricingService {

    private final Props props;
    private final LemonSqueezyProperties lsProps;

    public PricingService(Props props, LemonSqueezyProperties lsProps) {
        this.props = props;
        this.lsProps = lsProps;
    }

    public PricingDto currentPricing() {
        return PricingDto.builder().currency(props.getCurrency()).note(props.getNote())
                .tiers(List.of(
                        PricingTierDto.builder().name("Ücretsiz").monthly(0.0).yearly(0.0).build(),
                        PricingTierDto.builder().name("Standart").monthly(props.getStandardMonthly())
                                .yearly(props.getStandardYearly())
                                .variantMonthly(lsProps.getVariant().getStandardMonthly())
                                .variantYearly(lsProps.getVariant().getStandardYearly())
                                .build(),
                        PricingTierDto.builder().name("Pro").monthly(props.getProMonthly()).yearly(props.getProYearly())
                                .variantMonthly(lsProps.getVariant().getProMonthly())
                                .variantYearly(lsProps.getVariant().getProYearly())
                                .build()))
                .build();
    }

    public double priceFor(String plan, String period) {
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
