package com.qrmatik.server.controller;

import com.qrmatik.server.converter.TenantConverter;
import com.qrmatik.server.dto.TenantBootstrapUsersRequest;
import com.qrmatik.server.dto.TenantDto;
import com.qrmatik.server.dto.TenantInsertRequest;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.service.TenantAdminService;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenants")
public class TenantAdminController {

    private final TenantAdminService service;
    private final TenantConverter converter;

    public TenantAdminController(TenantAdminService service, TenantConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @GetMapping
    public List<TenantDto> list() {
        return service.list().stream().map(converter::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TenantInsertRequest req) {
        Optional<TenantEntity> created = service.create(req);
        if (created.isEmpty())
            return ResponseEntity.badRequest().body("Aynı işletme kodu ile yalnızca bir işletme oluşturulabilir");
        TenantEntity t = created.get();
        TenantDto dto = converter.toDto(t);

        // If admin user wasn't created for the new tenant, create a default admin
        // and return the credentials in the response for the operator.
        String defaultAdminUser = "admin";
        String defaultAdminPass = "admin123";
        var boot = new TenantBootstrapUsersRequest();
        boot.setAdminUsername(defaultAdminUser);
        boot.setAdminPassword(defaultAdminPass);
        service.bootstrapUsers(t.getCode(), boot);

        Map<String, Object> out = new HashMap<>();
        // flatten TenantDto fields to top-level for backward compatibility
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
        out.put("bootstrap", Map.of("username", defaultAdminUser, "password", defaultAdminPass));

        return ResponseEntity.created(URI.create("/api/tenants/" + t.getId())).body(out);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantDto> update(@PathVariable String id, @RequestBody TenantInsertRequest req) {
        Optional<TenantEntity> updated = service.update(id, req);
        return updated.map(e -> ResponseEntity.ok(converter.toDto(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean ok = service.delete(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{code}/bootstrap-users")
    public ResponseEntity<?> bootstrapUsers(@PathVariable String code, @RequestBody TenantBootstrapUsersRequest req) {
        boolean ok = service.bootstrapUsers(code, req);
        if (!ok)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }
}
