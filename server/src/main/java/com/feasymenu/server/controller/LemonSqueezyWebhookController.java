package com.feasymenu.server.controller;

import com.feasymenu.server.config.LemonSqueezyProperties;
import com.feasymenu.server.model.PlanType;
import com.feasymenu.server.model.TenantEntity;
import com.feasymenu.server.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping({"/api/webhooks/lemonsqueezy", "/api/webhooks/lemonsqueezy/"})
@Slf4j
public class LemonSqueezyWebhookController {

    private final TenantRepository tenantRepository;
    private final LemonSqueezyProperties lsProperties;

    public LemonSqueezyWebhookController(TenantRepository tenantRepository, LemonSqueezyProperties lsProperties) {
        this.tenantRepository = tenantRepository;
        this.lsProperties = lsProperties;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-Signature", required = false) String signature,
            @RequestBody Map<String, Object> payload) {

        String eventName = (String) ((Map) payload.get("meta")).get("event_name");
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");

        log.info("Received LemonSqueezy webhook: {}", eventName);

        if ("subscription_created".equals(eventName) || "subscription_updated".equals(eventName)
                || "order_created".equals(eventName)) {
            processSubscription(payload, attributes);
        }

        return ResponseEntity.ok("Received");
    }

    @GetMapping
    public ResponseEntity<String> testWebhook() {
        log.info("LemonSqueezy webhook GET test successful");
        return ResponseEntity.ok("LemonSqueezy Webhook Endpoint is Reachable. Please use POST for actual webhooks.");
    }

    private void processSubscription(Map<String, Object> payload, Map<String, Object> attributes) {
        Map<String, Object> meta = (Map<String, Object>) payload.get("meta");
        Map<String, Object> custom = null;

        if (meta != null && meta.get("custom_data") != null) {
            custom = (Map<String, Object>) meta.get("custom_data");
        } else if (attributes.get("custom_data") != null) {
            custom = (Map<String, Object>) attributes.get("custom_data");
        }

        if (custom == null || !custom.containsKey("tenant_code")) {
            log.warn("Webhook missing tenant_code in custom_data. Payload: {}", payload);
            return;
        }

        String tenantCode = String.valueOf(custom.get("tenant_code"));
        String variantId = String.valueOf(attributes.get("variant_id"));
        String status = (String) attributes.get("status");

        log.info("Processing LS webhook for tenant: {}, variant: {}, status: {}", tenantCode, variantId, status);
        String renewsAtStr = (String) attributes.get("renews_at");
        if (renewsAtStr == null)
            renewsAtStr = (String) attributes.get("ends_at");
        if (renewsAtStr == null)
            renewsAtStr = (String) attributes.get("created_at"); // Fallback for one-time orders

        Optional<TenantEntity> tOpt = tenantRepository.findByCode(tenantCode);
        if (tOpt.isPresent()) {
            TenantEntity t = tOpt.get();

            // Map Variant to Plan and Duration
            PlanType targetPlan = PlanType.FREE;
            boolean isYearly = false;

            if (variantId.equals(lsProperties.getVariant().getStandardMonthly())) {
                targetPlan = PlanType.STANDARD;
            } else if (variantId.equals(lsProperties.getVariant().getStandardYearly())) {
                targetPlan = PlanType.STANDARD;
                isYearly = true;
            } else if (variantId.equals(lsProperties.getVariant().getProMonthly())) {
                targetPlan = PlanType.PRO;
            } else if (variantId.equals(lsProperties.getVariant().getProYearly())) {
                targetPlan = PlanType.PRO;
                isYearly = true;
            }

            Instant newExpiration;

            // LS dates might have spaces instead of T, ensure ISO compatibility
            String isoDate = renewsAtStr.replace(" ", "T");
            if (!isoDate.contains("Z") && !isoDate.contains("+"))
                isoDate += "Z";

            Instant LSInstant = Instant.parse(isoDate);

            if (t.getPlanPaidUntil() != null && t.getPlanPaidUntil().isAfter(Instant.now())) {
                // Extend from current expiration
                newExpiration = isYearly
                        ? t.getPlanPaidUntil().plus(365, ChronoUnit.DAYS)
                        : t.getPlanPaidUntil().plus(30, ChronoUnit.DAYS);
            } else {
                // Start fresh from what LS says
                newExpiration = LSInstant;
                // If it's a one-time order and LS gave created_at, add the period
                if (attributes.containsKey("created_at") && renewsAtStr.equals(attributes.get("created_at"))) {
                    newExpiration = isYearly
                            ? LSInstant.plus(365, ChronoUnit.DAYS)
                            : LSInstant.plus(30, ChronoUnit.DAYS);
                }
            }

            if (status == null || "active".equals(status) || "on_trial".equals(status)) {
                t.setPlan(targetPlan);
                t.setPlanPaidUntil(newExpiration);
                t.setBillingPeriod(isYearly ? "YEARLY" : "MONTHLY");
                tenantRepository.save(t);
                log.info("Tenant {} upgraded to {} until {}", tenantCode, targetPlan, newExpiration);
            }
        }
    }
}
