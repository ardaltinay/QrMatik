package com.qrmatik.server.dto;

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

    // Optional bootstrap admin
    private String adminUsername;
    private String adminPassword;

    // Optional bootstrap kitchen & bar
    private String kitchenUsername;
    private String kitchenPassword;
    private String barUsername;
    private String barPassword;

}
