package com.feasymenu.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingTierDto {
    private String name; // e.g., Ücretsiz, Standart, Pro
    private Double monthly; // TRY per month
    private Double yearly; // TRY per year (optional)
    private String variantMonthly;
    private String variantYearly;
    private List<String> features;
}
