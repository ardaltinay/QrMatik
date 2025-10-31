package com.qrmatik.server.controller;

import com.qrmatik.server.converter.TenantConverter;
import com.qrmatik.server.dto.TenantDto;
import com.qrmatik.server.dto.TenantBootstrapUsersRequest;
import com.qrmatik.server.dto.TenantUpsertRequest;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.service.TenantAdminService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> create(@RequestBody TenantUpsertRequest req) {
        Optional<TenantEntity> created = service.create(req);
        if (created.isEmpty())
            return ResponseEntity.badRequest().body("Invalid or duplicate tenant code");
        TenantEntity t = created.get();
        TenantDto dto = converter.toDto(t);
        return ResponseEntity.created(URI.create("/api/tenants/" + t.getId())).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantDto> update(@PathVariable String id, @RequestBody TenantUpsertRequest req) {
        Optional<TenantEntity> updated = service.update(id, req);
        return updated.map(e -> ResponseEntity.ok(converter.toDto(e))).orElseGet(() -> ResponseEntity.notFound().build());
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
