package com.qrmatik.server.controller;

import com.qrmatik.server.converter.OrderConverter;
import com.qrmatik.server.dto.CreateOrderRequest;
import com.qrmatik.server.dto.OrderDto;
import com.qrmatik.server.dto.StatusUpdate;
import com.qrmatik.server.model.OrderEntity;
import com.qrmatik.server.service.OrderService;
import com.qrmatik.server.service.TenantContext;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderConverter converter;

    public OrderController(OrderService orderService, OrderConverter converter) {
        this.orderService = orderService;
        this.converter = converter;
    }

    @GetMapping
    public List<OrderDto> list() {
        String tenant = TenantContext.getTenant();
        List<OrderEntity> entities = orderService.listForTenant(tenant);
        return entities.stream().map(converter::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> get(@PathVariable String id) {
        Optional<OrderEntity> o = orderService.getById(id);
        return o.map(e -> ResponseEntity.ok(converter.toDto(e))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody CreateOrderRequest req) {
        String tenant = TenantContext.getTenant();
        Optional<OrderEntity> savedOpt = orderService.create(req, tenant);
        if (savedOpt.isEmpty())
            return ResponseEntity.badRequest().build();
        OrderEntity saved = savedOpt.get();
        return ResponseEntity.created(URI.create("/api/orders/" + saved.getId()))
                .header("X-Order-Session", saved.getSessionId())
                .header("X-Order-Expires",
                        saved.getSessionExpiresAt() != null ? String.valueOf(saved.getSessionExpiresAt()) : "")
                .body(converter.toDto(saved));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateStatus(@PathVariable String id, @RequestBody StatusUpdate payload) {
        String tenant = TenantContext.getTenant();
        Optional<OrderEntity> updated = orderService.updateStatus(id, tenant, payload.getStatus());
        return updated.map(orderEntity -> ResponseEntity.ok(converter.toDto(orderEntity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-table/{tableCode}")
    public List<OrderDto> byTable(@PathVariable String tableCode) {
        String tenant = TenantContext.getTenant();
        List<OrderEntity> entities = orderService.byTableForTenant(tableCode, tenant);
        return entities.stream().map(converter::toDto).collect(Collectors.toList());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> bySession(@PathVariable String sessionId) {
        String tenant = TenantContext.getTenant();
        List<OrderEntity> entities = orderService.bySessionForTenant(sessionId, tenant);
        // if any order indicates expired session, return 410 Gone
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        boolean expired = false;
        for (OrderEntity e : entities) {
            if (e.getSessionExpiresAt() != null && e.getSessionExpiresAt().isBefore(now)) {
                expired = true;
                break;
            }
        }
        if (expired) {
            return ResponseEntity.status(410).build();
        }
        return ResponseEntity.ok(entities.stream().map(converter::toDto).collect(Collectors.toList()));
    }

    @PostMapping("/close-table/{tableCode}")
    public ResponseEntity<?> closeTable(@PathVariable String tableCode) {
        String tenant = TenantContext.getTenant();
        int count = orderService.closeSessionsForTable(tableCode, tenant);
        return ResponseEntity.ok(java.util.Map.of("closed", count));
    }
}
