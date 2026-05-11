package com.feasymenu.server.repository;

import com.feasymenu.server.model.LoyaltyCampaignEntity;
import com.feasymenu.server.model.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoyaltyCampaignRepository extends JpaRepository<LoyaltyCampaignEntity, UUID> {
    Optional<LoyaltyCampaignEntity> findByTenant(TenantEntity tenant);
    Optional<LoyaltyCampaignEntity> findByTenant_Code(String tenantCode);
}
