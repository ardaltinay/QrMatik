package com.feasymenu.server.dto;

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
    private String nameEn;
    private String description;
    private String descriptionEn;
    private BigDecimal price;
    private BigDecimal priceUsd;
    private String category;
    private String subcategory;
    private String image;
    private Boolean stockEnabled;
    private Integer stockQuantity;
}
