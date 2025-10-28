package com.qrmatik.server.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineDto {
    private UUID itemId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String image;
    private String category;
    private String subcategory;
}
