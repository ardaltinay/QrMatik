package com.qrmatik.server.controller;

import com.qrmatik.server.exception.PlanLimitExceededException;
import com.qrmatik.server.service.PlanGuard;
import com.qrmatik.server.service.TenantContext;
import com.qrmatik.server.service.TenantService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenant")
public class TenantController {

    private final TenantService tenantService;
    private final PlanGuard planGuard;

    public TenantController(TenantService tenantService, PlanGuard planGuard) {
        this.tenantService = tenantService;
        this.planGuard = planGuard;
    }

    @GetMapping("/config")
    public Map<String, Object> config(@RequestParam(value = "code", required = false) String code) {
        return tenantService.getConfig(code, TenantContext.getTenant());
    }

    @PutMapping("/branding")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBranding(@RequestBody Map<String, String> req) {
        try {
            String tenant = TenantContext.getTenant();
            String primary = req != null ? req.getOrDefault("primaryColor", null) : null;
            String accent = req != null ? req.getOrDefault("accentColor", null) : null;
            String logoUrl = req != null ? req.getOrDefault("logoUrl", null) : null;
            if (logoUrl != null && !logoUrl.trim().isEmpty()) {
                planGuard.assertCanUploadLogo(tenant);
            }
            var out = tenantService.updateBranding(tenant, primary, accent, logoUrl);
            return ResponseEntity.ok(out);
        } catch (PlanLimitExceededException ex) {
            return ResponseEntity.status(402).body(ex.getMessage());
        }
    }
}
