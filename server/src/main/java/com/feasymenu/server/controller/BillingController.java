package com.feasymenu.server.controller;

import com.feasymenu.server.dto.SchedulePlanChangeRequest;
import com.feasymenu.server.model.PlanType;
import com.feasymenu.server.repository.TenantRepository;
import com.feasymenu.server.service.TenantContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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

    @PostMapping("/api/billing/schedule-downgrade")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> scheduleDowngrade(@Valid @RequestBody SchedulePlanChangeRequest req) {
        String tenant = TenantContext.getTenant();
        if (tenant == null || tenant.isBlank())
            return ResponseEntity.status(401).body(Map.of("message", "Tenant bulunamadı"));
        var tOpt = tenantRepository.findByCode(tenant);
        if (tOpt.isEmpty())
            return ResponseEntity.status(404).body(Map.of("message", "Tenant bulunamadı"));
        var t = tOpt.get();
        PlanType currentPlan = t.getPlan() == null ? PlanType.FREE : t.getPlan();
        PlanType targetPlan = PlanType.fromString(req.getPlan());
        String targetPeriod = req.getBillingPeriod() == null ? null : req.getBillingPeriod().trim().toUpperCase();
        if (targetPeriod != null && !"YEARLY".equalsIgnoreCase(targetPeriod)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Sadece yıllık faturalama desteklenmektedir"));
        }
        if (targetPlan == null)
            return ResponseEntity.badRequest().body(Map.of("message", "Geçersiz plan"));
        int rank = planRank(currentPlan) - planRank(targetPlan);
        boolean planDown = rank > 0;
        if (!planDown) {
            return ResponseEntity.badRequest().body(Map.of("message", "Bu istek downgrade değil"));
        }
        if (t.getPlanPaidUntil() == null) {
            t.setPlan(targetPlan);
            t.setBillingPeriod("YEARLY");
            t.setPendingPlan(null);
            t.setPendingBillingPeriod(null);
            t.setPendingEffectiveDate(null);
            t.setPlanPaidUntil(null);
            tenantRepository.save(t);
            return ResponseEntity.ok(Map.of("applied", true, "plan", targetPlan.name(), "billingPeriod", targetPeriod));
        }
        LocalDate eff = t.getPlanPaidUntil().plusDays(1);
        t.setPendingPlan(targetPlan);
        t.setPendingBillingPeriod("YEARLY");
        t.setPendingEffectiveDate(eff);
        tenantRepository.save(t);
        return ResponseEntity.ok(Map.of("scheduled", true, "effectiveDate", String.valueOf(eff)));
    }

    private int planRank(PlanType p) {
        return switch (p) {
            case FREE -> 0;
            case STANDARD -> 1;
            case PRO -> 2;
        };
    }
}
