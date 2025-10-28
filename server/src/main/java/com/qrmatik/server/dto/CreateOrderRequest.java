package com.qrmatik.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateOrderRequest {
    private String tableCode; // optional
    private String sessionId; // optional, server can create if missing
    private String status;    // optional, default NEW
    private List<CreateOrderLine> lines; // required at least 1

    @Getter
    @Setter
    public static class CreateOrderLine {
        private UUID itemId;        // preferred
        private Integer quantity;   // defaults to 1
        private BigDecimal price;   // optional, fallback to MenuItem.price
        private String name;        // optional, fallback to MenuItem.name
    }
}
