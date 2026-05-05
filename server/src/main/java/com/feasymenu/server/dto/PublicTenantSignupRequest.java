package com.feasymenu.server.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PublicTenantSignupRequest {
    private String code;
    private String name;
    private String ownerName;
    private String ownerEmail;
    private String logoUrl;
    private String primaryColor;
    private String accentColor;
    private String config; // optional JSON
    private String ownerPassword;

    // Deprecated bootstrap fields
    private String adminUsername;
    private String adminPassword;
    private String kitchenUsername;
    private String kitchenPassword;
    private String barUsername;
    private String barPassword;
}
