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
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/public")
public class PublicSignupController {

    private static final String DEFAULT_PRIMARY_COLOR = "#94a684";
    private static final String DEFAULT_ACCENT_COLOR = "#788a68";
    private static final Pattern CODE_RE = Pattern.compile("^(?=.{1,63}$)[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
    private static final Pattern EMAIL_RE = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final TenantAdminService service;
    private final TenantConverter converter;

    public PublicSignupController(TenantAdminService service, TenantConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @PostMapping("/tenant-signup")
    public ResponseEntity<?> signup(@RequestBody PublicTenantSignupRequest req) {
        if (req.getCode() == null || req.getCode().isBlank()) {
            return validationError("code", "Isletme kodu zorunludur.");
        }
        if (!CODE_RE.matcher(req.getCode().trim()).matches()) {
            return validationError("code", "Isletme kodu yalnizca kucuk harf, rakam ve tire icerebilir.");
        }
        if (req.getOwnerName() == null || req.getOwnerName().isBlank()) {
            return validationError("ownerName", "Isletme sahibi adi zorunludur.");
        }
        if (req.getOwnerEmail() == null || req.getOwnerEmail().isBlank()) {
            return validationError("ownerEmail", "E-posta adresi zorunludur.");
        }
        if (!EMAIL_RE.matcher(req.getOwnerEmail().trim()).matches()) {
            return validationError("ownerEmail", "Gecerli bir e-posta adresi giriniz.");
        }
        if (req.getOwnerPassword() == null || req.getOwnerPassword().length() < 8) {
            return validationError("ownerPassword", "Sifre en az 8 karakter olmalidir.");
        }

        String primaryColor = (req.getPrimaryColor() == null || req.getPrimaryColor().isBlank())
            ? DEFAULT_PRIMARY_COLOR
            : req.getPrimaryColor();
        String accentColor = (req.getAccentColor() == null || req.getAccentColor().isBlank())
            ? DEFAULT_ACCENT_COLOR
            : req.getAccentColor();

        TenantInsertRequest insert = new TenantInsertRequest();
        insert.setCode(req.getCode());
        insert.setName(req.getName());
        insert.setOwnerName(req.getOwnerName());
        insert.setOwnerEmail(req.getOwnerEmail());
        insert.setLogoUrl(req.getLogoUrl());
        insert.setPrimaryColor(primaryColor);
        insert.setAccentColor(accentColor);
        insert.setPlan("FREE");

        Optional<TenantEntity> created = service.create(insert);
        if (created.isEmpty()) {
            return validationError("code", "Bu isletme kodu zaten kullanimda.");
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

    private ResponseEntity<Map<String, Object>> validationError(String field, String message) {
        Map<String, Object> out = new HashMap<>();
        out.put("field", field);
        out.put("message", message);
        out.put("code", "validation_error");
        return ResponseEntity.badRequest().body(out);
    }
}
