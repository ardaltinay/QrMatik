package com.qrmatik.server.dto;

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
public class TenantDto {
    private String id;
    private String code;
    private String name;
    private String ownerName;
    private String ownerEmail;
    private String logoUrl;
    private String primaryColor;
    private String accentColor;
    private String config; // raw JSON string
}
