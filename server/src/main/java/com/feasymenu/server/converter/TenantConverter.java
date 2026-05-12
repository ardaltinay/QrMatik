package com.feasymenu.server.converter;

import com.feasymenu.server.dto.TenantDto;
import com.feasymenu.server.model.TenantEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantConverter {
    public TenantDto toDto(TenantEntity e) {
        if (e == null)
            return null;
        return TenantDto.builder().id(e.getId() != null ? e.getId().toString() : null).code(e.getCode())
                .name(e.getName()).ownerName(e.getOwnerName()).ownerEmail(e.getOwnerEmail()).logoUrl(e.getLogoUrl())
                .primaryColor(e.getPrimaryColor()).accentColor(e.getAccentColor()).welcomeMessage(e.getWelcomeMessage())
                .fontFamily(e.getFontFamily()).plan(e.getPlan() != null ? e.getPlan().name() : null)
                .customDomain(e.getCustomDomain()).active(e.isActive())
                .createdAt(e.getCreatedTime() != null ? e.getCreatedTime().toString() : null).build();
    }

    public UUID parseId(String id) {
        try {
            return UUID.fromString(id);
        } catch (Exception ex) {
            return null;
        }
    }
}
