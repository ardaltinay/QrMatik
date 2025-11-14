package com.qrmatik.server.controller;

import com.qrmatik.server.dto.SchedulePlanChangeRequest;
import com.qrmatik.server.model.PlanType;
import com.qrmatik.server.repository.TenantRepository;
import com.qrmatik.server.service.TenantContext;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillingController {

    private final TenantRepository tenantRepository;

    @Value("${payments.contactEmail:qrmatik.cloud@gmail.com}")
    private String paymentsContactEmail;

    public BillingController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @PostMapping("/api/billing/checkout/init")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> initializeCheckout() {
        // Hosted checkout removed — instruct frontend to use manual flow.
        return ResponseEntity.ok(Map.of("paymentDisabled", true, "contactEmail", paymentsContactEmail));
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
