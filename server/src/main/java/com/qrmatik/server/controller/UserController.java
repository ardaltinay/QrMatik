package com.qrmatik.server.controller;

import com.qrmatik.server.converter.UserConverter;
import com.qrmatik.server.dto.UserDto;
import com.qrmatik.server.dto.UserInsertRequest;
import com.qrmatik.server.exception.PlanFeatureUnavailableException;
import com.qrmatik.server.model.UserEntity;
import com.qrmatik.server.service.TenantContext;
import com.qrmatik.server.service.UserService;
import java.net.URI;
import java.util.List;
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
        try {
            UserEntity saved = userService.create(req, tenant);
            return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).body(converter.toDto(saved));
        } catch (PlanFeatureUnavailableException ex) {
            return ResponseEntity.status(402).body(new UserDto());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable String id, @RequestBody UserInsertRequest req) {
        String tenant = TenantContext.getTenant();
        try {
            Optional<UserEntity> updated = userService.update(id, req, tenant);
            return updated.map(u -> ResponseEntity.ok(converter.toDto(u)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (PlanFeatureUnavailableException ex) {
            return ResponseEntity.status(402).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        String tenant = TenantContext.getTenant();
        boolean ok = userService.delete(id, tenant);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
