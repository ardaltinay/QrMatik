package com.qrmatik.server.controller;

import com.qrmatik.server.converter.UserConverter;
import com.qrmatik.server.dto.UserDto;
import com.qrmatik.server.dto.UserUpsertRequest;
import com.qrmatik.server.model.UserEntity;
import com.qrmatik.server.service.TenantContext;
import com.qrmatik.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserConverter converter;

    public UserController(UserService userService, UserConverter converter) {
        this.userService = userService;
        this.converter = converter;
    }

    @GetMapping
    public List<UserDto> list() {
        String tenant = TenantContext.getTenant();
        return userService.listForTenant(tenant).stream().map(converter::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserUpsertRequest req) {
        String tenant = TenantContext.getTenant();
        UserEntity saved = userService.create(req, tenant);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).body(converter.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable String id, @RequestBody UserUpsertRequest req) {
        String tenant = TenantContext.getTenant();
        Optional<UserEntity> updated = userService.update(id, req, tenant);
        return updated.map(u -> ResponseEntity.ok(converter.toDto(u))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        String tenant = TenantContext.getTenant();
        boolean ok = userService.delete(id, tenant);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
