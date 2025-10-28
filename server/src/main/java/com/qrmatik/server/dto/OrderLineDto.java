package com.qrmatik.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

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
