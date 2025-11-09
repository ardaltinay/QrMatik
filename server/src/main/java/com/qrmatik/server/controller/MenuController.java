package com.qrmatik.server.controller;

import com.qrmatik.server.converter.MenuItemConverter;
import com.qrmatik.server.dto.MenuItemDto;
import com.qrmatik.server.exception.PlanFeatureUnavailableException;
import com.qrmatik.server.exception.PlanLimitExceededException;
import com.qrmatik.server.model.MenuItemEntity;
import com.qrmatik.server.service.MenuService;
import com.qrmatik.server.service.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;
    private final MenuItemConverter converter;
    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    public MenuController(MenuService menuService, MenuItemConverter converter) {
        this.menuService = menuService;
        this.converter = converter;
    }

    @GetMapping
    public List<MenuItemDto> list() {
        String tenant = TenantContext.getTenant();
        return menuService.listForTenant(tenant).stream().map(converter::toDto).toList();
    }

    @GetMapping("/popular")
    public List<MenuItemDto> popular(@RequestParam(name = "limit", defaultValue = "4") int limit) {
        String tenant = TenantContext.getTenant();
        return menuService.popular(tenant, limit).stream().map(converter::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MenuItemEntity m) {
        try {
            String tenant = TenantContext.getTenant();
            MenuItemEntity saved = menuService.create(m, tenant);
            return ResponseEntity.created(URI.create("/api/menu/" + saved.getId())).body(converter.toDto(saved));
        } catch (PlanLimitExceededException ex) {
            return ResponseEntity.status(402).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemDto> update(@PathVariable UUID id, @RequestBody MenuItemEntity patch) {
        String tenant = TenantContext.getTenant();
        try {
            return menuService.update(id, patch, tenant).map(e -> ResponseEntity.ok(converter.toDto(e)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (PlanFeatureUnavailableException ex) {
            return ResponseEntity.status(402).build();
        }
    }

    @PostMapping(path = "/{id}/image", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        try {
            String tenant = TenantContext.getTenant();
            Map<String, Object> result = menuService.uploadImage(id, file, tenant);
            if (result == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok().body(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            log.error("Image upload failed for menu item {}: {}", id, e.getMessage(), e);
            String msg = root.getMessage() != null ? root.getMessage() : e.getClass().getSimpleName();
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        String tenant = TenantContext.getTenant();
        boolean ok = menuService.delete(id, tenant);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
