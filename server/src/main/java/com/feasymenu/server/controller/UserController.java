package com.feasymenu.server.controller;

import com.feasymenu.server.converter.UserConverter;
import com.feasymenu.server.dto.UserDto;
import com.feasymenu.server.dto.UserInsertRequest;
import com.feasymenu.server.model.UserEntity;
import com.feasymenu.server.service.TenantContext;
import com.feasymenu.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<UserDto> create(@RequestBody UserInsertRequest req) {
        String tenant = TenantContext.getTenant();
        UserEntity saved = userService.create(req, tenant);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).body(converter.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable String id, @RequestBody UserInsertRequest req) {
        String tenant = TenantContext.getTenant();
        Optional<UserEntity> updated = userService.update(id, req, tenant);
        return updated.map(u -> ResponseEntity.ok(converter.toDto(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        String tenant = TenantContext.getTenant();
        boolean ok = userService.delete(id, tenant);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
