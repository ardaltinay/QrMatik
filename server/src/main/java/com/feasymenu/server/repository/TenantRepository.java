package com.feasymenu.server.repository;

import com.feasymenu.server.model.PlanType;
import com.feasymenu.server.model.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {
    Optional<TenantEntity> findByCode(String code);

    Optional<TenantEntity> findByCodeIgnoreCase(String code);

    Optional<TenantEntity> findByCustomDomain(String customDomain);

    List<TenantEntity> findByPendingEffectiveDateLessThanEqual(Instant date);

    List<TenantEntity> findByPlanNotAndPlanPaidUntil(PlanType plan, Instant date);

    List<TenantEntity> findByPlanNotAndPlanPaidUntilBetween(PlanType plan, Instant start, Instant end);

    List<TenantEntity> findByPlanNotAndPlanPaidUntilLessThan(PlanType plan, Instant date);
}
