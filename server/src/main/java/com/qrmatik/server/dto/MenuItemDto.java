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
public class MenuItemDto {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String category;
    private String subcategory;
    private String image;
    private Boolean stockEnabled;
    private Integer stockQuantity;
}
