package com.feasymenu.server.controller;

import com.feasymenu.server.converter.TenantConverter;
import com.feasymenu.server.dto.PublicTenantSignupRequest;
import com.feasymenu.server.dto.TenantBootstrapUsersRequest;
import com.feasymenu.server.dto.TenantDto;
import com.feasymenu.server.dto.TenantInsertRequest;
import com.feasymenu.server.model.TenantEntity;
import com.feasymenu.server.service.TenantAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        if (req.getCode() == null || req.getCode().isBlank() || req.getOwnerName() == null
                || req.getOwnerName().isBlank() || req.getOwnerEmail() == null || req.getOwnerEmail().isBlank()
                || req.getOwnerPassword() == null || req.getOwnerPassword().length() < 8
                || req.getPrimaryColor() == null || req.getPrimaryColor().isBlank() || req.getAccentColor() == null
                || req.getAccentColor().isBlank()) {
            return ResponseEntity.badRequest().body("Missing required fields or password too short");
        }

        TenantInsertRequest insert = new TenantInsertRequest();
        insert.setCode(req.getCode());
        insert.setName(req.getName());
        insert.setOwnerName(req.getOwnerName());
        insert.setOwnerEmail(req.getOwnerEmail());
        insert.setLogoUrl(req.getLogoUrl());
        insert.setPrimaryColor(req.getPrimaryColor());
        insert.setAccentColor(req.getAccentColor());
        insert.setPlan("FREE");

        Optional<TenantEntity> created = service.create(insert);
        if (created.isEmpty()) {
            return ResponseEntity.badRequest().body("Aynı işletme kodu ile yalnızca bir işletme oluşturulabilir");
        }

        TenantEntity t = created.get();

        // Create the admin user using owner's email and password
        TenantBootstrapUsersRequest boot = new TenantBootstrapUsersRequest();
        boot.setAdminUsername(req.getOwnerEmail());
        boot.setAdminPassword(req.getOwnerPassword());
        service.bootstrapUsers(t.getCode(), boot);

        TenantDto dto = converter.toDto(t);

        Map<String, Object> out = new HashMap<>();
        out.put("id", dto.getId());
        out.put("code", dto.getCode());
        out.put("name", dto.getName());
        out.put("ownerName", dto.getOwnerName());
        out.put("ownerEmail", dto.getOwnerEmail());
        out.put("logoUrl", dto.getLogoUrl());
        out.put("primaryColor", dto.getPrimaryColor());
        out.put("accentColor", dto.getAccentColor());
        out.put("plan", dto.getPlan());
        out.put("customDomain", dto.getCustomDomain());

        return ResponseEntity.created(URI.create("/api/tenants/" + t.getId())).body(out);
    }
}
