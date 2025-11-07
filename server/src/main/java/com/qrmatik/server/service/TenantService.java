package com.qrmatik.server.service;

import com.qrmatik.server.exception.BadRequestException;
import com.qrmatik.server.exception.NotFoundException;
import com.qrmatik.server.repository.TenantRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TenantService {
    private final TenantRepository repository;

    public TenantService(TenantRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getConfig(String codeOrNull, String currentTenantOrNull) {
        String resolved = codeOrNull;
        if (resolved == null || resolved.isBlank())
            resolved = currentTenantOrNull;
        if (resolved == null)
            throw new NotFoundException("Tenant not found");
        String key = resolved.trim();
        var t = repository.findByCode(key).or(() -> repository.findByCodeIgnoreCase(key))
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        Map<String, Object> out = new HashMap<>();
        out.put("code", t.getCode());
        out.put("primaryColor", t.getPrimaryColor());
        out.put("accentColor", t.getAccentColor());
        out.put("name", t.getName());
        out.put("logoUrl", t.getLogoUrl());
        out.put("config", t.getConfigJson());
        out.put("plan", t.getPlan() != null ? t.getPlan().name() : null);
        out.put("billingPeriod", t.getBillingPeriod());
        out.put("planPaidUntil", t.getPlanPaidUntil());
        out.put("customDomain", t.getCustomDomain());
        out.put("locale", t.getLocale() != null ? t.getLocale() : "tr-TR");
        out.put("timeZone", t.getTimeZone() != null ? t.getTimeZone() : "Europe/Istanbul");
        out.put("pendingPlan", t.getPendingPlan() != null ? t.getPendingPlan().name() : null);
        out.put("pendingBillingPeriod", t.getPendingBillingPeriod());
        out.put("pendingEffectiveDate", t.getPendingEffectiveDate());
        return out;
    }

    public Map<String, Object> updateBranding(String tenantCode, String primaryColor, String accentColor,
            String logoUrl) {
        if (tenantCode == null || tenantCode.isBlank())
            throw new BadRequestException("Missing tenant");
        var t = repository.findByCode(tenantCode).orElseThrow(() -> new NotFoundException("Tenant not found"));
        if (primaryColor != null && !primaryColor.isBlank())
            t.setPrimaryColor(primaryColor.trim());
        if (accentColor != null && !accentColor.isBlank())
            t.setAccentColor(accentColor.trim());
        if (logoUrl != null) {
            String trimmed = logoUrl.trim();
            if (trimmed.isEmpty()) {
                t.setLogoUrl(null);
            } else {
                t.setLogoUrl(trimmed);
            }
        }
        repository.save(t);
        Map<String, Object> out = new HashMap<>();
        out.put("code", t.getCode());
        out.put("primaryColor", t.getPrimaryColor());
        out.put("accentColor", t.getAccentColor());
        out.put("logoUrl", t.getLogoUrl());
        out.put("plan", t.getPlan() != null ? t.getPlan().name() : null);
        out.put("locale", t.getLocale() != null ? t.getLocale() : "tr-TR");
        out.put("timeZone", t.getTimeZone() != null ? t.getTimeZone() : "Europe/Istanbul");
        return out;
    }
}
