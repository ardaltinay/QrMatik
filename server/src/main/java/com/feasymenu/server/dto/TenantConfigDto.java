package com.feasymenu.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantConfigDto {
    private String code;
    private String name;
    private String plan;
    private String customDomain;
    private String primaryColor;
    private String accentColor;
    private String locale; // e.g., tr-TR
    private String timeZone; // e.g., Europe/Istanbul
    private String address;
    private String phone;
    private String ownerEmail;
}
