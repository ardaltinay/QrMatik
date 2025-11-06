package com.qrmatik.server.model;

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

@Entity
@Table(
    name = "tables",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"tenant_id", "code"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableEntity extends AbstractEntity {
  @Column(nullable = false)
  private String code; // e.g. A1, B3, Bar-01

  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id")
  private TenantEntity tenant;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @Builder.Default
  private TableStatus status = TableStatus.AVAILABLE;
}
