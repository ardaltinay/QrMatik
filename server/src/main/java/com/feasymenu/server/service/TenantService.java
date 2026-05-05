package com.feasymenu.server.service;

import com.feasymenu.server.exception.BadRequestException;
import com.feasymenu.server.exception.NotFoundException;
import com.feasymenu.server.repository.TenantRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class TenantService {
    private final TenantRepository repository;

    public TenantService(TenantRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "tenants", key = "#codeOrNull != null ? #codeOrNull : #currentTenantOrNull")
    public Map<String, Object> getConfig(String codeOrNull, String currentTenantOrNull) {
        String resolved = codeOrNull;
        if (resolved == null || resolved.isBlank())
            resolved = currentTenantOrNull;
        if (resolved == null)
            throw new NotFoundException("Tenant not found");
        String key = resolved.trim();
        var t = repository.findByCode(key).or(() -> repository.findByCodeIgnoreCase(key))
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        if (!t.isActive()) {
            throw new NotFoundException("Tenant is suspended");
        }

        Map<String, Object> out = new HashMap<>();
        out.put("code", t.getCode());
        out.put("primaryColor", t.getPrimaryColor());
        out.put("accentColor", t.getAccentColor());
        out.put("name", t.getName());
        out.put("logoUrl", t.getLogoUrl());
        out.put("welcomeMessage", t.getWelcomeMessage());
        out.put("fontFamily", t.getFontFamily());
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

    @CacheEvict(value = "tenants", key = "#tenantCode")
    public Map<String, Object> updateBranding(String tenantCode, String primaryColor, String accentColor,
            String logoUrl, String welcomeMessage, String fontFamily) {
        if (tenantCode == null || tenantCode.isBlank())
            throw new BadRequestException("Missing tenant");
        var t = repository.findByCode(tenantCode).orElseThrow(() -> new NotFoundException("Tenant not found"));
        if (!t.isActive()) {
            throw new BadRequestException("Tenant is suspended");
        }
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
        if (welcomeMessage != null) {
            String trimmed = welcomeMessage.trim();
            t.setWelcomeMessage(trimmed.isEmpty() ? null : trimmed);
        }
        if (fontFamily != null) {
            String trimmed = fontFamily.trim();
            t.setFontFamily(trimmed.isEmpty() ? null : trimmed);
        }
        repository.save(t);
        Map<String, Object> out = new HashMap<>();
        out.put("code", t.getCode());
        out.put("primaryColor", t.getPrimaryColor());
        out.put("accentColor", t.getAccentColor());
        out.put("logoUrl", t.getLogoUrl());
        out.put("welcomeMessage", t.getWelcomeMessage());
        out.put("fontFamily", t.getFontFamily());
        out.put("plan", t.getPlan() != null ? t.getPlan().name() : null);
        out.put("locale", t.getLocale() != null ? t.getLocale() : "tr-TR");
        out.put("timeZone", t.getTimeZone() != null ? t.getTimeZone() : "Europe/Istanbul");
        return out;
    }
}
