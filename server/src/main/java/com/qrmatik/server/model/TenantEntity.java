package com.qrmatik.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantEntity extends AbstractEntity {
    @Column(nullable = false, unique = true)
    private String code; // e.g. 'demo-restaurant'

    private String name;

    // Business owner contact
    private String ownerName;

    private String ownerEmail;

    private String logoUrl;

    private String primaryColor;

    private String accentColor;

    @Lob
    private String configJson;
}
