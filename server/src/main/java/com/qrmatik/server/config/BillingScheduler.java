package com.qrmatik.server.config;

import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
public class BillingScheduler {
    private static final Logger log = LoggerFactory.getLogger(BillingScheduler.class);

    private final TenantRepository tenantRepository;
    private final ZoneId appZoneId;

    public BillingScheduler(TenantRepository tenantRepository, ZoneId appZoneId) {
        this.tenantRepository = tenantRepository;
        this.appZoneId = appZoneId;
    }

    // Run daily shortly after midnight (app timezone) to apply scheduled downgrades
    @Scheduled(cron = "0 5 0 * * *", zone = "${app.time-zone:Europe/Istanbul}")
    public void applyScheduledPlanChanges() {
        LocalDate today = LocalDate.now(appZoneId);
        List<TenantEntity> due = tenantRepository.findByPendingEffectiveDateLessThanEqual(today);
        if (due.isEmpty())
            return;
        int count = 0;
        for (TenantEntity t : due) {
            try {
                if (t.getPendingPlan() == null && t.getPendingBillingPeriod() == null)
                    continue;
                // apply scheduled change
                if (t.getPendingPlan() != null)
                    t.setPlan(t.getPendingPlan());
                if (t.getPendingBillingPeriod() != null)
                    t.setBillingPeriod(t.getPendingBillingPeriod());
                // After period end, there is no paid period active
                t.setPlanPaidUntil(null);
                t.setPendingPlan(null);
                t.setPendingBillingPeriod(null);
                t.setPendingEffectiveDate(null);
                tenantRepository.save(t);
                count++;
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Failed to apply scheduled plan change for tenant {}: {}", t.getCode(), e.getMessage());
                }
            }
        }
        if (count > 0 && log.isInfoEnabled()) {
            log.info("Applied scheduled plan changes for {} tenants", count);
        }
    }
}
