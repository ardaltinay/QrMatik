package com.feasymenu.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loyalty_campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyCampaignEntity extends AbstractEntity {

    @OneToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = false;

    @Column(name = "prizes_json", columnDefinition = "TEXT")
    private String prizesJson; // Stores List<LoyaltyPrizeDto> as JSON
}
