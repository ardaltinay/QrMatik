package com.qrmatik.server.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
