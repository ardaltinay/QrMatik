package com.feasymenu.server.controller;

import com.feasymenu.server.converter.OrderConverter;
import com.feasymenu.server.dto.CancelRequest;
import com.feasymenu.server.dto.CallWaiterRequest;
import com.feasymenu.server.dto.CreateOrderRequest;
import com.feasymenu.server.dto.OrderDto;
import com.feasymenu.server.dto.RequestBillRequest;
import com.feasymenu.server.dto.StatusUpdate;
import com.feasymenu.server.model.OrderEntity;
import com.feasymenu.server.service.OrderService;
import com.feasymenu.server.service.TenantContext;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable String id,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
        Optional<OrderEntity> o = orderService.getIfViewable(id, sessionId, authenticated);
        return o.map(orderEntity -> ResponseEntity.ok(converter.toDto(orderEntity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateOrderRequest req) {
        String tenant = TenantContext.getTenant();
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
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateStatus(@PathVariable String id, @RequestBody StatusUpdate payload,
            @RequestParam(name = "target", required = false) String target) {
        String tenant = TenantContext.getTenant();
        Optional<OrderEntity> updated = orderService.updateStatus(id, tenant, payload.getStatus(), target);
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
    public ResponseEntity<?> bySession(@PathVariable String sessionId,
            @RequestParam(name = "tableCode", required = false) String tableCode) {
        String tenant = TenantContext.getTenant();
        var res = orderService.bySessionForView(sessionId, tenant, appZoneId, tableCode);
        if (!res.orders().isEmpty() && !res.anyNonExpired()) {
            return ResponseEntity.status(410).body(Map.of("message", "session.expired"));
        }
        return ResponseEntity.ok(res.orders().stream().map(converter::toDto).collect(Collectors.toList()));
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

    @PostMapping("/call-waiter")
    public ResponseEntity<?> callWaiter(@RequestBody CallWaiterRequest req) {
        String tenant = TenantContext.getTenant();
        orderService.callWaiter(req.getTableCode(), tenant);
        return ResponseEntity.ok(Map.of("message", "Waiter call notification sent"));
    }
}
