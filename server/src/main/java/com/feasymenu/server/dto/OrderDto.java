package com.feasymenu.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private String tableCode;
    private String sessionId;
    private String tenantCode;
    private String customerName;
    private String status;
    private String kitchenStatus;
    private String barStatus;
    private BigDecimal total;
    private Instant createdTime;
    // Zone-aware timestamp for clients; prefer this over createdTime when available
    private OffsetDateTime createdAt;
    private Instant sessionExpiresAt;
    private List<OrderLineDto> lines;
}
