package com.qrmatik.server.controller;

import com.qrmatik.server.converter.TenantConverter;
import com.qrmatik.server.dto.PublicTenantSignupRequest;
import com.qrmatik.server.dto.TenantBootstrapUsersRequest;
import com.qrmatik.server.dto.TenantDto;
import com.qrmatik.server.dto.TenantInsertRequest;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.service.TenantAdminService;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                || req.getPrimaryColor() == null || req.getPrimaryColor().isBlank() || req.getAccentColor() == null
                || req.getAccentColor().isBlank()) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        TenantInsertRequest insert = new TenantInsertRequest();
        insert.setCode(req.getCode());
        insert.setName(req.getName());
        insert.setOwnerName(req.getOwnerName());
        insert.setOwnerEmail(req.getOwnerEmail());
        insert.setLogoUrl(req.getLogoUrl());
        insert.setPrimaryColor(req.getPrimaryColor());
        insert.setAccentColor(req.getAccentColor());
        insert.setConfig(req.getConfig());

        Optional<TenantEntity> created = service.create(insert);
        if (created.isEmpty()) {
            return ResponseEntity.badRequest().body("Aynı işletme kodu ile yalnızca bir işletme oluşturulabilir");
        }

        TenantEntity t = created.get();

        // Optionally bootstrap users if provided (admin, kitchen, bar)
        if ((req.getAdminUsername() != null && !req.getAdminUsername().isBlank())
                || (req.getKitchenUsername() != null && !req.getKitchenUsername().isBlank())
                || (req.getBarUsername() != null && !req.getBarUsername().isBlank())) {
            TenantBootstrapUsersRequest boot = new TenantBootstrapUsersRequest();
            if (req.getAdminUsername() != null && !req.getAdminUsername().isBlank() && req.getAdminPassword() != null
                    && !req.getAdminPassword().isBlank()) {
                boot.setAdminUsername(req.getAdminUsername());
                boot.setAdminPassword(req.getAdminPassword());
            }
            if (req.getKitchenUsername() != null && !req.getKitchenUsername().isBlank()
                    && req.getKitchenPassword() != null && !req.getKitchenPassword().isBlank()) {
                boot.setKitchenUsername(req.getKitchenUsername());
                boot.setKitchenPassword(req.getKitchenPassword());
            }
            if (req.getBarUsername() != null && !req.getBarUsername().isBlank() && req.getBarPassword() != null
                    && !req.getBarPassword().isBlank()) {
                boot.setBarUsername(req.getBarUsername());
                boot.setBarPassword(req.getBarPassword());
            }
            service.bootstrapUsers(t.getCode(), boot);
        }

        TenantDto dto = converter.toDto(t);

        // If no admin was provided in the signup request, create a default admin user
        // and return the credentials to the caller so the owner can log in.
        boolean createdDefaultAdmin = false;
        String defaultAdminUser = "admin";
        String defaultAdminPass = "admin123";
        if ((req.getAdminUsername() == null || req.getAdminUsername().isBlank())
                && (req.getAdminPassword() == null || req.getAdminPassword().isBlank())) {
            var boot = new TenantBootstrapUsersRequest();
            boot.setAdminUsername(defaultAdminUser);
            boot.setAdminPassword(defaultAdminPass);
            service.bootstrapUsers(t.getCode(), boot);
            createdDefaultAdmin = true;
        }

        Map<String, Object> out = new HashMap<>();
        // flatten TenantDto fields to top-level for backward compatibility with
        // frontend
        out.put("id", dto.getId());
        out.put("code", dto.getCode());
        out.put("name", dto.getName());
        out.put("ownerName", dto.getOwnerName());
        out.put("ownerEmail", dto.getOwnerEmail());
        out.put("logoUrl", dto.getLogoUrl());
        out.put("primaryColor", dto.getPrimaryColor());
        out.put("accentColor", dto.getAccentColor());
        out.put("config", dto.getConfig());
        out.put("plan", dto.getPlan());
        out.put("customDomain", dto.getCustomDomain());

        if (createdDefaultAdmin) {
            out.put("bootstrap", Map.of("username", defaultAdminUser, "password", defaultAdminPass));
        }

        return ResponseEntity.created(URI.create("/api/tenants/" + t.getId())).body(out);
    }
}
