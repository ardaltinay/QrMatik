package com.qrmatik.server.controller;

import com.qrmatik.server.converter.OrderConverter;
import com.qrmatik.server.dto.CancelRequest;
import com.qrmatik.server.dto.CreateOrderRequest;
import com.qrmatik.server.dto.OrderDto;
import com.qrmatik.server.dto.RequestBillRequest;
import com.qrmatik.server.dto.StatusUpdate;
import com.qrmatik.server.exception.TableUnavailableException;
import com.qrmatik.server.model.OrderEntity;
import com.qrmatik.server.service.OrderService;
import com.qrmatik.server.service.TenantContext;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ZoneId appZoneId;

    public OrderController(OrderService orderService, OrderConverter converter, ZoneId appZoneId) {
        this.orderService = orderService;
        this.converter = converter;
        this.appZoneId = appZoneId;
    }

    @GetMapping
    public List<OrderDto> list() {
        String tenant = TenantContext.getTenant();
        List<OrderEntity> entities = orderService.listForTenant(tenant);
        return entities.stream().map(converter::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> get(@PathVariable String id,
            @org.springframework.web.bind.annotation.RequestParam(name = "sid", required = false) String sid) {
        Optional<OrderEntity> o = orderService.getById(id);
        if (o.isEmpty())
            return ResponseEntity.notFound().build();
        OrderEntity e = o.get();
        // If unauthenticated, require valid session id to view order details
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
        if (!authenticated) {
            if (sid == null || sid.isBlank()) {
                return ResponseEntity.notFound().build();
            }
            String realSid = e.getSessionId();
            if (realSid == null || !realSid.equals(sid)) {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(converter.toDto(e));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateOrderRequest req) {
        String tenant = TenantContext.getTenant();
        try {
            Optional<OrderEntity> savedOpt = orderService.create(req, tenant);
            if (savedOpt.isEmpty())
                return ResponseEntity.badRequest().build();
            OrderEntity saved = savedOpt.get();
            String expIso = "";
            if (saved.getSessionExpiresAt() != null) {
                try {
                    // Serialize expiry as UTC instant (ISO 8601 with Z) to avoid timezone ambiguity
                    // on clients
                    expIso = saved.getSessionExpiresAt().atZone(appZoneId).toInstant().toString();
                } catch (Exception ignore) {
                    expIso = String.valueOf(saved.getSessionExpiresAt());
                }
            }
            return ResponseEntity.created(URI.create("/api/orders/" + saved.getId()))
                    .header("X-Order-Session", saved.getSessionId()).header("X-Order-Expires", expIso)
                    .body(converter.toDto(saved));
        } catch (TableUnavailableException ex) {
            return ResponseEntity.status(423).body(Map.of("message", ex.getMessage()));
        }
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
        // Session is considered expired only if ALL orders in the session are expired
        // This ensures canceling a single order (which sets its sessionExpiresAt to
        // now)
        // doesn't hide other active orders from the same session.
        LocalDateTime now = LocalDateTime.now(appZoneId);
        boolean anyNonExpired = false;
        for (OrderEntity e : entities) {
            boolean isExpired = (e.getSessionExpiresAt() != null && e.getSessionExpiresAt().isBefore(now));
            if (!isExpired) {
                anyNonExpired = true;
                break;
            }
        }
        if (!entities.isEmpty() && !anyNonExpired) {
            return ResponseEntity.status(410).build();
        }
        return ResponseEntity.ok(entities.stream().map(converter::toDto).collect(Collectors.toList()));
    }

    @PostMapping("/close-table/{tableCode}")
    public ResponseEntity<?> closeTable(@PathVariable String tableCode) {
        String tenant = TenantContext.getTenant();
        int count = orderService.closeSessionsForTable(tableCode, tenant);
        return ResponseEntity.ok(Map.of("closed", count));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String id, @RequestBody CancelRequest req) {
        String tenant = TenantContext.getTenant();
        String sessionId = (req != null ? req.getSessionId() : null);
        Optional<OrderEntity> updated = orderService.cancelBySession(id, tenant, sessionId);
        return updated.map(e -> ResponseEntity.ok(converter.toDto(e)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/{id}/request-bill")
    public ResponseEntity<?> requestBill(@PathVariable String id, @RequestBody RequestBillRequest req) {
        String tenant = TenantContext.getTenant();
        String sessionId = (req != null ? req.getSessionId() : null);
        Optional<OrderEntity> updated = orderService.requestBillBySession(id, tenant, sessionId);
        return updated.map(e -> ResponseEntity.ok(converter.toDto(e)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
