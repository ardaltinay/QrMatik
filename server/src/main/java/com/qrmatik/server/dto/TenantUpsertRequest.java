package com.qrmatik.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TenantUpsertRequest {
    private String code;
    private String name;
    private String logoUrl;
    private String primaryColor;
    private String accentColor;
    private String config; // raw JSON string (optional)
}
