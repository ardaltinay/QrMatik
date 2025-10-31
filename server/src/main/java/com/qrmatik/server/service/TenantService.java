package com.qrmatik.server.service;

import com.qrmatik.server.repository.TenantRepository;
import java.util.Map;
import java.util.HashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found");
    String key = resolved.trim();
    var t = repository.findByCode(key)
        .or(() -> repository.findByCodeIgnoreCase(key))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));

    // Build DTO map allowing nullable fields
    Map<String, Object> out = new HashMap<>();
    out.put("code", t.getCode()); // required
    out.put("primaryColor", t.getPrimaryColor()); // required by contract
    out.put("accentColor", t.getAccentColor()); // required by contract
    // optional fields: can be null
    out.put("name", t.getName());
    out.put("logoUrl", t.getLogoUrl());
    out.put("config", t.getConfigJson());
    return out;
    }
}
