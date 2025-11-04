package com.qrmatik.server.controller;

import com.qrmatik.server.dto.PublicTenantSignupRequest;
import com.qrmatik.server.dto.TenantBootstrapUsersRequest;
import com.qrmatik.server.dto.TenantDto;
import com.qrmatik.server.dto.TenantInsertRequest;
import com.qrmatik.server.converter.TenantConverter;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.service.TenantAdminService;
import java.net.URI;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicSignupController {

    private final TenantAdminService service;
    private final TenantConverter converter;

    public PublicSignupController(TenantAdminService service, TenantConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @PostMapping("/tenant-signup")
    public ResponseEntity<?> signup(@RequestBody PublicTenantSignupRequest req) {
        // Basic validation
        if (req.getCode() == null || req.getCode().isBlank() || req.getPrimaryColor() == null
                || req.getPrimaryColor().isBlank() || req.getAccentColor() == null
                || req.getAccentColor().isBlank()) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

    TenantInsertRequest insert = new TenantInsertRequest();
    insert.setCode(req.getCode());
    insert.setName(req.getName());
    insert.setLogoUrl(req.getLogoUrl());
    insert.setPrimaryColor(req.getPrimaryColor());
    insert.setAccentColor(req.getAccentColor());
    insert.setConfig(req.getConfig());

    Optional<TenantEntity> created = service.create(insert);
        if (created.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or duplicate tenant code");
        }

        TenantEntity t = created.get();

        // Optionally bootstrap admin user if provided
        if (req.getAdminUsername() != null && !req.getAdminUsername().isBlank()
                && req.getAdminPassword() != null && !req.getAdminPassword().isBlank()) {
            TenantBootstrapUsersRequest boot = new TenantBootstrapUsersRequest();
            boot.setAdminUsername(req.getAdminUsername());
            boot.setAdminPassword(req.getAdminPassword());
            service.bootstrapUsers(t.getCode(), boot);
        }

        TenantDto dto = converter.toDto(t);
        return ResponseEntity.created(URI.create("/api/tenants/" + t.getId())).body(dto);
    }
}
