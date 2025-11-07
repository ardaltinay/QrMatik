package com.qrmatik.server.dto;

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
    private Integer monthly; // TRY per month
    private Integer yearly; // TRY per year (optional)
    private List<String> features;
}
