package com.feasymenu.server.controller;

import com.feasymenu.server.repository.TenantRepository;
import com.feasymenu.server.service.TenantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BillingController {

    private final TenantRepository tenantRepository;

    @Value("${payments.contactEmail:support@feasymenu.com}")
    private String paymentsContactEmail;

    public BillingController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Value("${lemonsqueezy.storeId:}")
    private String storeId;

    @GetMapping("/api/billing/checkout/init")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<?> initializeCheckout() {
        String tenantCode = TenantContext.getTenant();
        Map<String, Object> response = new HashMap<>();
        response.put("storeId", storeId);
        response.put("tenantCode", tenantCode);
        response.put("paymentEnabled", true);

        // Include current plan info so the frontend knows which plan is active
        if (tenantCode != null) {
            tenantRepository.findByCode(tenantCode).ifPresent(t -> {
                response.put("currentPlan", t.getPlan() == null ? "FREE" : t.getPlan().name());
                response.put("billingPeriod", t.getBillingPeriod());
            });
        }
        if (!response.containsKey("currentPlan")) {
            response.put("currentPlan", "FREE");
        }
        return ResponseEntity.ok(response);
    }
}
