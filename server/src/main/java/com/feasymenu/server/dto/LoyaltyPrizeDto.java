package com.feasymenu.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPrizeDto {
    private String label;
    private Integer discountPercent;
    private String color;
    private Integer weight; // For probability logic later
}
