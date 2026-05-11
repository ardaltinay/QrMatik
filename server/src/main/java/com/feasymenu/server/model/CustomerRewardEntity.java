package com.feasymenu.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "customer_rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class CustomerRewardEntity extends AbstractEntity {

    private String email;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "reward_code", unique = true)
    private String rewardCode;

    @Column(name = "prize_label")
    private String prizeLabel;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Column(name = "is_used")
    private Boolean isUsed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private TenantEntity tenant;
}
