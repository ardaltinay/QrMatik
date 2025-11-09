package com.qrmatik.server.service;

import com.qrmatik.server.exception.PlanLimitExceededException;
import com.qrmatik.server.exception.PlanFeatureUnavailableException;
import com.qrmatik.server.model.PlanType;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.repository.MenuItemRepository;
import com.qrmatik.server.repository.TableRepository;
import com.qrmatik.server.repository.TenantRepository;
import org.springframework.stereotype.Component;

@Component
public class PlanGuard {
    private final TenantRepository tenantRepository;
    private final MenuItemRepository menuItemRepository;
    private final TableRepository tableRepository;

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
        long limit = switch (plan) {
            case FREE -> 50L;
            case STANDARD -> 500L;
            case PRO -> Long.MAX_VALUE;
        };
        if (current >= limit) {
            throw new PlanLimitExceededException("Plan limitine ulaşıldı: limit: " + limit);
        }
    }

    public void assertCanCreateTable(String tenantCode) {
        TenantEntity t = tenantRepository.findByCode(tenantCode).orElse(null);
        if (t == null)
            return;
        PlanType plan = t.getPlan() == null ? PlanType.FREE : t.getPlan();
        long current = tableRepository.countByTenant_Code(tenantCode);
        // NOTE: FREE limit temporarily reduced to 1 for testing
        long limit = switch (plan) {
            case FREE -> 1L;
            case STANDARD -> 50L;
            case PRO -> Long.MAX_VALUE;
        };
        if (current >= limit) {
            throw new PlanLimitExceededException("Plan limitine ulaşıldı: limit: " + limit);
        }
    }

    public void assertCanUploadLogo(String tenantCode) {
        TenantEntity t = tenantRepository.findByCode(tenantCode).orElse(null);
        if (t == null)
            return;
        PlanType plan = t.getPlan() == null ? PlanType.FREE : t.getPlan();
        // Logo yükleme yalnızca Standart ve Pro planlarda
        if (plan == PlanType.FREE) {
            throw new PlanLimitExceededException("Logo özelleştirme Standart/Pro planlarda mevcuttur.");
        }
    }

    public void assertStockFeature(String tenantCode) {
        TenantEntity t = tenantRepository.findByCode(tenantCode).orElse(null);
        if (t == null)
            return;
        PlanType plan = t.getPlan() == null ? PlanType.FREE : t.getPlan();
        if (plan != PlanType.PRO) {
            throw new PlanFeatureUnavailableException("Stok kontrolü yalnızca Pro planda mevcuttur.");
        }
    }
}
