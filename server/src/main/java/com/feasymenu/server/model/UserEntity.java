package com.feasymenu.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"tenant_id", "username"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Filter(name = "tenantFilter", condition = "tenant_id = (SELECT t.id FROM tenants t WHERE t.code = :tenantId)")
public class UserEntity extends AbstractEntity {
    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private TenantEntity tenant;

    @Column(name = "password_hash")
    private String passwordHash;
}
