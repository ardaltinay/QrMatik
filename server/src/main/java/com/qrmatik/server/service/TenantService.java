package com.qrmatik.server.service;

import com.qrmatik.server.repository.TenantRepository;
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
            return Map.of("code", "default", "name", "Default");
        return repository.findByCode(resolved)
                .map(t -> Map.<String, Object>of("code", t.getCode(), "name", t.getName(), "logoUrl", t.getLogoUrl(),
                        "primaryColor", t.getPrimaryColor(), "accentColor", t.getAccentColor(), "config",
                        t.getConfigJson()))
                .orElse(Map.<String, Object>of("code", "default", "name", "Default"));
    }
}
