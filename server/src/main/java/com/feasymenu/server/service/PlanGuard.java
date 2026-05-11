package com.feasymenu.server.service;

import com.feasymenu.server.exception.PlanFeatureUnavailableException;
import com.feasymenu.server.exception.PlanLimitExceededException;
import com.feasymenu.server.model.PlanType;
import com.feasymenu.server.model.TenantEntity;
import com.feasymenu.server.repository.MenuItemRepository;
import com.feasymenu.server.repository.TableRepository;
import com.feasymenu.server.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PlanGuard {
    private final TenantRepository tenantRepository;
    private final MenuItemRepository menuItemRepository;
    private final TableRepository tableRepository;
    private static final Logger log = LoggerFactory.getLogger(PlanGuard.class);

    public PlanGuard(TenantRepository tenantRepository, MenuItemRepository menuItemRepository,
            TableRepository tableRepository) {
        this.tenantRepository = tenantRepository;
        this.menuItemRepository = menuItemRepository;
        this.tableRepository = tableRepository;
    }

    public void assertCanCreateMenuItem(String tenantCode) {
        TenantEntity t = tenantRepository.findByCode(tenantCode).orElse(null);
        if (t == null)
            return; // if no tenant, let other guards fail elsewhere
        PlanType plan = t.getPlan() == null ? PlanType.FREE : t.getPlan();
        long current = menuItemRepository.countByTenant_Code(tenantCode);
        long limit = menuItemLimit(plan);
        if (log.isDebugEnabled()) {
            log.debug("MenuItem limit check tenant={} plan={} current={} limit={}", tenantCode, plan, current, limit);
        }
        if (current >= limit) {
            throw new PlanLimitExceededException("error.plan.menuItemLimit");
        }
    }

    public void assertCanCreateTable(String tenantCode) {
        TenantEntity t = tenantRepository.findByCode(tenantCode).orElse(null);
        if (t == null)
            return;
        PlanType plan = t.getPlan() == null ? PlanType.FREE : t.getPlan();
        long current = tableRepository.countByTenant_Code(tenantCode);
        long limit = tableLimit(plan);
        if (log.isDebugEnabled()) {
            log.debug("Table limit check tenant={} plan={} current={} limit={}", tenantCode, plan, current, limit);
        }
        if (current >= limit) {
            throw new PlanLimitExceededException("error.plan.tableLimit");
        }
    }

    public void assertCanUploadLogo(String tenantCode) {
        TenantEntity t = tenantRepository.findByCode(tenantCode).orElse(null);
        if (t == null)
            return;
        PlanType plan = t.getPlan() == null ? PlanType.FREE : t.getPlan();
        if (plan == PlanType.FREE) {
            throw new PlanLimitExceededException("error.plan.logoUploadProOrStandard");
        }
    }

    public void assertStockFeature(String tenantCode) {
        TenantEntity t = tenantRepository.findByCode(tenantCode).orElse(null);
        if (t == null)
            return;
        PlanType plan = t.getPlan() == null ? PlanType.FREE : t.getPlan();
        if (plan != PlanType.PRO) {
            throw new PlanFeatureUnavailableException("error.plan.stockControlPro");
        }
    }

    public void assertPaidPlan(String tenantCode) {
        TenantEntity t = tenantRepository.findByCode(tenantCode).orElse(null);
        if (t == null)
            return;
        PlanType plan = t.getPlan() == null ? PlanType.FREE : t.getPlan();
        if (plan == PlanType.FREE) {
            throw new PlanFeatureUnavailableException("error.plan.paidPlanRequired");
        }
    }

    // ---- Helper limit calculators (centralized to avoid divergence) ----
    public long tableLimit(PlanType plan) {
        return switch (plan) {
            case FREE -> 10L;
            case STANDARD -> 50L;
            case PRO -> Long.MAX_VALUE;
        };
    }

    public long menuItemLimit(PlanType plan) {
        return switch (plan) {
            case FREE -> 50L;
            case STANDARD -> 500L;
            case PRO -> Long.MAX_VALUE;
        };
    }

}
