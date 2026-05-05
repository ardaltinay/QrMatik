package com.feasymenu.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockUpdateRequest {
    private Boolean stockEnabled;
    private Integer stockQuantity;
}
