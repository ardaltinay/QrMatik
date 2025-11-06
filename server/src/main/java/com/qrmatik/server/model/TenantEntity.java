package com.qrmatik.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
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

  @Lob private String configJson;

  @Enumerated(EnumType.STRING)
  @Column(nullable = true)
  @Builder.Default
  private PlanType plan = PlanType.FREE;

  // Optional custom domain for PRO plan tenants (e.g., menu.mybistro.com)
  @Column(name = "custom_domain")
  private String customDomain;

  // Optional i18n/timezone per tenant
  @Column(name = "locale")
  private String locale; // e.g., tr-TR, en-US

  @Column(name = "time_zone")
  private String timeZone; // e.g., Europe/Istanbul

  // Billing tracking (set on successful upgrade payments)
  // MONTHLY | YEARLY
  @Column(name = "billing_period")
  private String billingPeriod;

  // Date until which the plan is paid/active (inclusive)
  @Column(name = "plan_paid_until")
  private LocalDate planPaidUntil;

  @Column(name = "last_payment_id")
  private String lastPaymentId;

  @Column(name = "last_payment_at")
  private Instant lastPaymentAt;

  // Scheduled plan change at period end (for downgrades)
  @Enumerated(EnumType.STRING)
  @Column(name = "pending_plan")
  private PlanType pendingPlan;

  @Column(name = "pending_billing_period")
  private String pendingBillingPeriod; // MONTHLY | YEARLY

  @Column(name = "pending_effective_date")
  private LocalDate pendingEffectiveDate;
}
