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
public class PricingDto {
    private String currency; // e.g., TRY
    private String note; // optional marketing note
    private List<PricingTierDto> tiers;
}
