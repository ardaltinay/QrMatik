package com.feasymenu.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateCodeResponseDto {
    private String code;
    private String description;
    private Integer discountPercent;
    private boolean valid;
}
