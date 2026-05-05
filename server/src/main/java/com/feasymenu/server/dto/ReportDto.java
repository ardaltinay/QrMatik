package com.feasymenu.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDto {
    private Summary summary;
    private List<TrendPoint> trend;
    private List<TopProduct> topProducts;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private BigDecimal revenue;
        private Double revenueGrowth; // percentage
        private Integer orders;
        private Double ordersGrowth;
        private Integer cancelled;
        private Double cancelledGrowth;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrendPoint {
        private String label;
        private BigDecimal value;
        private Integer percentage; // for visual height
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopProduct {
        private String name;
        private String category;
        private Integer count;
        private BigDecimal revenue;
        private String image;
    }
}
