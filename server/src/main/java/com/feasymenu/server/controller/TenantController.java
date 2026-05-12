package com.feasymenu.server.controller;

import com.feasymenu.server.exception.BadRequestException;
import com.feasymenu.server.exception.NotFoundException;
import com.feasymenu.server.exception.PlanLimitExceededException;
import com.feasymenu.server.service.PlanGuard;
import com.feasymenu.server.service.TenantContext;
import com.feasymenu.server.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    public ResponseEntity<?> updateBranding(@RequestBody Map<String, Object> req) {
        try {
            String tenant = TenantContext.getTenant();
            String primary = req != null ? (String) req.getOrDefault("primaryColor", null) : null;
            String accent = req != null ? (String) req.getOrDefault("accentColor", null) : null;
            String logoUrl = req != null ? (String) req.getOrDefault("logoUrl", null) : null;
            String welcomeMessage = req != null ? (String) req.getOrDefault("welcomeMessage", null) : null;
            String fontFamily = req != null ? (String) req.getOrDefault("fontFamily", null) : null;
            String address = req != null ? (String) req.getOrDefault("address", null) : null;
            String phone = req != null ? (String) req.getOrDefault("phone", null) : null;

            // New Geo fields
            Double latitude = null;
            Double longitude = null;
            Integer locationThreshold = null;

            if (req != null) {
                if (req.get("latitude") != null)
                    latitude = Double.valueOf(req.get("latitude").toString());
                if (req.get("longitude") != null)
                    longitude = Double.valueOf(req.get("longitude").toString());
                if (req.get("locationThreshold") != null)
                    locationThreshold = Integer.valueOf(req.get("locationThreshold").toString());
            }

            if (logoUrl != null && !logoUrl.trim().isEmpty()) {
                planGuard.assertCanUploadLogo(tenant);
            }
            var out = tenantService.updateBranding(tenant, primary, accent, logoUrl, welcomeMessage, fontFamily,
                    address, phone, latitude, longitude, locationThreshold);
            return ResponseEntity.ok(out);
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (NotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (PlanLimitExceededException ex) {
            return ResponseEntity.status(402).body(ex.getMessage());
        }
    }

    @PutMapping("/category-order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategoryOrder(@RequestBody Map<String, String> req) {
        String tenant = TenantContext.getTenant();
        String order = req != null ? req.get("order") : null;
        tenantService.updateCategoryOrder(tenant, order);
        return ResponseEntity.ok().build();
    }
}
