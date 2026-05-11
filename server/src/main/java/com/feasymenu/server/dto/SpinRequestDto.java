package com.feasymenu.server.dto;

import lombok.Data;

@Data
public class SpinRequestDto {
    private String email;
    private String prizeLabel;
    private Integer discountPercent;
}
