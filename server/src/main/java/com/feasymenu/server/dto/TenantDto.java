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
public class TenantDto {
    private String id;
    private String code;
    private String name;
    private String ownerName;
    private String ownerEmail;
    private String logoUrl;
    private String primaryColor;
    private String accentColor;
    private String welcomeMessage;
    private String fontFamily;
    private String plan; // FREE | STANDARD | PRO
    private String customDomain; // optional, PRO only
    private boolean active;
    private String createdAt;
}
