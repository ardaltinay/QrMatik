package com.qrmatik.server.converter;

import com.qrmatik.server.dto.TenantDto;
import com.qrmatik.server.model.TenantEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TenantConverter {
  public TenantDto toDto(TenantEntity e) {
    if (e == null) return null;
    return TenantDto.builder()
        .id(e.getId() != null ? e.getId().toString() : null)
        .code(e.getCode())
        .name(e.getName())
        .ownerName(e.getOwnerName())
        .ownerEmail(e.getOwnerEmail())
        .logoUrl(e.getLogoUrl())
        .primaryColor(e.getPrimaryColor())
        .accentColor(e.getAccentColor())
        .config(e.getConfigJson())
        .plan(e.getPlan() != null ? e.getPlan().name() : null)
        .customDomain(e.getCustomDomain())
        .build();
  }

  public UUID parseId(String id) {
    try {
      return UUID.fromString(id);
    } catch (Exception ex) {
      return null;
    }
  }
}
