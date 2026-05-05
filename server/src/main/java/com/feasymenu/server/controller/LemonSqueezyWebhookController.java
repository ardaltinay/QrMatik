package com.feasymenu.server.controller;

import com.feasymenu.server.model.TenantEntity;
import com.feasymenu.server.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks/lemonsqueezy")
@Slf4j
public class LemonSqueezyWebhookController {

    private final TenantRepository tenantRepository;

    @Value("${lemonsqueezy.webhookSecret:}")
    private String webhookSecret;

    public LemonSqueezyWebhookController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-Signature", required = false) String signature,
            @RequestBody Map<String, Object> payload) {

        // TODO: Validate signature if secret is provided

        String eventName = (String) ((Map) payload.get("meta")).get("event_name");
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");

        log.info("Received LemonSqueezy webhook: {}", eventName);

        if ("subscription_created".equals(eventName) || "subscription_updated".equals(eventName)) {
            processSubscription(attributes);
        }

        return ResponseEntity.ok("Received");
    }

    private void processSubscription(Map<String, Object> attributes) {
        // LemonSqueezy allows passing custom data in the checkout URL as 'passthrough'
        // or 'custom'
        // We will pass the tenant code there.
        Map<String, Object> custom = (Map<String, Object>) attributes.get("custom_data");
        if (custom == null || !custom.containsKey("tenant_code")) {
            log.warn("Webhook missing tenant_code in custom_data");
            return;
        }

        String tenantCode = (String) custom.get("tenant_code");
        String variantId = String.valueOf(attributes.get("variant_id"));
        String status = (String) attributes.get("status"); // active, on_trial, expired, etc.
        String endsAt = (String) attributes.get("renews_at");
        if (endsAt == null)
            endsAt = (String) attributes.get("ends_at");

        Optional<TenantEntity> tOpt = tenantRepository.findByCode(tenantCode);
        if (tOpt.isPresent()) {
            TenantEntity t = tOpt.get();

            // Map variantId to PlanType (This mapping should be configured)
            // For now, let's assume we have a way to know which variant is which plan.
            // We can add this logic once the user provides Variant IDs.

            if ("active".equals(status) || "on_trial".equals(status)) {
                log.info("Upgrading tenant {} based on variant {}", tenantCode, variantId);
                // t.setPlan(PlanType.STANDARD); // Example
                // t.setPlanPaidUntil(LocalDate.parse(endsAt.substring(0, 10)));
                // tenantRepository.save(t);
            }
        }
    }
}
