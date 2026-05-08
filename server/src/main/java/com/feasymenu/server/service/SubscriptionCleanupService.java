package com.feasymenu.server.service;

import com.feasymenu.server.model.PlanType;
import com.feasymenu.server.model.TenantEntity;
import com.feasymenu.server.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionCleanupService {

    private final TenantRepository tenantRepository;
    private final NotificationService notificationService;

    /**
     * Checks for expiring and expired subscriptions daily at 09:00 AM.
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkExpirations() {
        log.info("Starting subscription expiration check...");
        Instant now = Instant.now();

        // 1. Check for 3 days warning
        notifyTenantsExpiringBetween(now.plus(2, ChronoUnit.DAYS), now.plus(3, ChronoUnit.DAYS), "3 days");

        // 2. Check for 1 day warning
        notifyTenantsExpiringBetween(now, now.plus(1, ChronoUnit.DAYS), "1 day");

        // 3. Downgrade expired tenants
        downgradeExpiredTenants(now);

        log.info("Subscription expiration check completed.");
    }

    private void notifyTenantsExpiringBetween(Instant start, Instant end, String label) {
        List<TenantEntity> tenants = tenantRepository.findByPlanNotAndPlanPaidUntilBetween(PlanType.FREE, start, end);
        for (TenantEntity t : tenants) {
            log.info("Processing expiration warning for tenant: {}", t.getCode());
            notificationService.sendExpirationWarning(t.getOwnerEmail(), t.getName(), label);
        }
    }

    private void downgradeExpiredTenants(Instant now) {
        // Find all non-free tenants whose paid_until is BEFORE now
        List<TenantEntity> expired = tenantRepository.findByPlanNotAndPlanPaidUntilLessThan(PlanType.FREE, now);
        for (TenantEntity t : expired) {
            log.warn("EXPIRED: Tenant {} ({}) subscription expired on {}. Downgrading to FREE plan.",
                    t.getName(), t.getCode(), t.getPlanPaidUntil());

            t.setPlan(PlanType.FREE);
            t.setBillingPeriod(null);
            t.setPlanPaidUntil(null);
            tenantRepository.save(t);

            notificationService.sendPlanDowngradedNotification(t.getOwnerEmail(), t.getName());
        }
    }
}
