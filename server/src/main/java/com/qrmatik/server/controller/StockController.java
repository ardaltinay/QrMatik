package com.qrmatik.server.controller;

import com.qrmatik.server.converter.MenuItemConverter;
import com.qrmatik.server.dto.MenuItemDto;
import com.qrmatik.server.dto.StockUpdateRequest;
import com.qrmatik.server.exception.PlanFeatureUnavailableException;
import com.qrmatik.server.model.MenuItemEntity;
import com.qrmatik.server.service.MenuService;
import com.qrmatik.server.service.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final MenuService menuService;
    private final MenuItemConverter converter;

    public StockController(MenuService menuService, MenuItemConverter converter) {
        this.menuService = menuService;
        this.converter = converter;
    }

    @GetMapping("/items")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> list() {
        String tenant = TenantContext.getTenant();
        try {
            List<MenuItemDto> items = menuService.listForStock(tenant).stream().map(converter::toDto).toList();
            return ResponseEntity.ok(items);
        } catch (PlanFeatureUnavailableException ex) {
            return ResponseEntity.status(402).body(ex.getMessage());
        }
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody StockUpdateRequest req) {
        String tenant = TenantContext.getTenant();
        try {
            MenuItemEntity patch = new MenuItemEntity();
            patch.setStockEnabled(req.getStockEnabled());
            patch.setStockQuantity(req.getStockQuantity());
            Optional<MenuItemEntity> updated = menuService.update(id, patch, tenant);
            return updated.map(e -> ResponseEntity.ok(converter.toDto(e)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (PlanFeatureUnavailableException ex) {
            return ResponseEntity.status(402).body(ex.getMessage());
        }
    }
}
