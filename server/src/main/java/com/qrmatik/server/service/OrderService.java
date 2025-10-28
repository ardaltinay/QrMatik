package com.qrmatik.server.service;

import com.qrmatik.server.dto.CreateOrderRequest;
import com.qrmatik.server.model.*;
import com.qrmatik.server.repository.MenuItemRepository;
import com.qrmatik.server.repository.OrderRepository;
import com.qrmatik.server.repository.TableRepository;
import com.qrmatik.server.repository.TenantRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;
    private final TenantRepository tenantRepository;

    public OrderService(OrderRepository orderRepository, TableRepository tableRepository,
            MenuItemRepository menuItemRepository, TenantRepository tenantRepository) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.menuItemRepository = menuItemRepository;
        this.tenantRepository = tenantRepository;
    }

    public List<OrderEntity> listForTenant(String tenant) {
        return (tenant == null) ? orderRepository.findAll() : orderRepository.findByTenant_Code(tenant);
    }

    public Optional<OrderEntity> getById(String id) {
        try {
            return orderRepository.findById(UUID.fromString(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<OrderEntity> byTableForTenant(String tableCode, String tenant) {
        if (tenant == null)
            return orderRepository.findByTable_Code(tableCode);
        return orderRepository.findByTable_CodeAndTenant_Code(tableCode, tenant);
    }

    public List<OrderEntity> bySessionForTenant(String sessionId, String tenant) {
        return (tenant == null)
                ? orderRepository.findBySessionId(sessionId)
                : orderRepository.findBySessionIdAndTenant_Code(sessionId, tenant);
    }

    public Optional<OrderEntity> updateStatus(String id, String tenant, String status) {
        Optional<OrderEntity> o = getById(id);
        if (o.isEmpty())
            return Optional.empty();
        if (tenant != null) {
            TenantEntity ordTenant = o.get().getTenant();
            String oc = (ordTenant != null ? ordTenant.getCode() : null);
            if (oc != null && !tenant.equals(oc))
                return Optional.empty();
        }
        var parsed = OrderStatus.fromString(status);
        if (parsed == null)
            return Optional.empty();
        OrderEntity order = o.get();
        order.setStatus(parsed);
        // adjust session expiry based on status
        if (parsed == OrderStatus.SERVED) {
            order.setSessionExpiresAt(LocalDateTime.now().plusHours(3));
        } else if (parsed == OrderStatus.PAYMENT_COMPLETED) {
            order.setSessionExpiresAt(LocalDateTime.now());
        } else {
            // sliding window: ensure at least 24h from now
            LocalDateTime min = LocalDateTime.now().plusHours(24);
            if (order.getSessionExpiresAt() == null || order.getSessionExpiresAt().isBefore(min)) {
                order.setSessionExpiresAt(min);
            }
        }
        OrderEntity saved = orderRepository.save(order);
        // After payment completed, if no active orders remain on this table, mark table
        // AVAILABLE
        if (parsed == OrderStatus.PAYMENT_COMPLETED) {
            try {
                updateTableAvailabilityForTable(saved.getTable(), tenant);
            } catch (Exception ignore) {
            }
        }
        return Optional.of(saved);
    }

    public Optional<OrderEntity> create(CreateOrderRequest req, String tenant) {
        OrderEntity input = new OrderEntity();
        input.setCreatedTime(LocalDateTime.now());
        input.setStatus(req.getStatus() != null ? OrderStatus.fromString(req.getStatus()) : OrderStatus.NEW);
        input.setSessionExpiresAt(LocalDateTime.now().plusHours(24));
        String incomingSession = req.getSessionId();
        input.setSessionId((incomingSession == null || incomingSession.isBlank())
                ? java.util.UUID.randomUUID().toString()
                : incomingSession);
        if (tenant != null) {
            tenantRepository.findByCode(tenant).ifPresent(input::setTenant);
        }

        // validate tableCode if provided and set relation
        if (req.getTableCode() != null && !req.getTableCode().isBlank()) {
            var tableOpt = (tenant != null)
                    ? tableRepository.findByCodeAndTenant_Code(req.getTableCode(), tenant)
                    : tableRepository.findByCode(req.getTableCode());
            if (tableOpt.isEmpty()) {
                return Optional.empty();
            }
            var table = tableOpt.get();
            input.setTable(table);
            // On order creation, mark table AVAILABLE per requirement
            if (table.getStatus() != com.qrmatik.server.model.TableStatus.AVAILABLE) {
                table.setStatus(com.qrmatik.server.model.TableStatus.AVAILABLE);
                tableRepository.save(table);
            }
        }

        buildOrderLinesFromRequest(input, tenant, req);

        if ((input.getTotal() == null || BigDecimal.ZERO.compareTo(input.getTotal()) == 0) && input.getLines() != null
                && !input.getLines().isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            for (OrderItemEntity li : input.getLines()) {
                if (li.getUnitPrice() != null && li.getQuantity() != null) {
                    total = total.add(li.getUnitPrice().multiply(BigDecimal.valueOf(li.getQuantity())));
                }
            }
            input.setTotal(total);
        }

        return Optional.of(orderRepository.save(input));
    }

    public int closeSessionsForTable(String tableCode, String tenant) {
        // Mark table as UNAVAILABLE
        try {
            var tOpt = (tenant == null || tenant.isBlank())
                    ? tableRepository.findByCode(tableCode)
                    : tableRepository.findByCodeAndTenant_Code(tableCode, tenant);
            if (tOpt.isPresent()) {
                var t = tOpt.get();
                if (t.getStatus() != com.qrmatik.server.model.TableStatus.UNAVAILABLE) {
                    t.setStatus(com.qrmatik.server.model.TableStatus.UNAVAILABLE);
                    tableRepository.save(t);
                }
            }
        } catch (Exception ignored) {
        }

        List<OrderEntity> list;
        list = (tenant == null)
                ? orderRepository.findByTable_Code(tableCode)
                : orderRepository.findByTable_CodeAndTenant_Code(tableCode, tenant);
        LocalDateTime now = LocalDateTime.now();
        int count = 0;
        for (OrderEntity e : list) {
            boolean changed = false;
            if (e.getSessionExpiresAt() == null || e.getSessionExpiresAt().isAfter(now)) {
                e.setSessionExpiresAt(now);
                changed = true;
            }
            // Optionally normalize status on close-table: if not paid, at least mark as
            // SERVED
            if (e.getStatus() != null && e.getStatus() != OrderStatus.PAYMENT_COMPLETED
                    && e.getStatus() != OrderStatus.SERVED) {
                e.setStatus(OrderStatus.SERVED);
                changed = true;
            }
            if (changed) {
                orderRepository.save(e);
                count++;
            }
        }
        return count;
    }

    private void buildOrderLinesFromRequest(OrderEntity input, String tenant, CreateOrderRequest req) {
        List<OrderItemEntity> lines = new ArrayList<>();
        if (req.getLines() == null)
            return;
        for (CreateOrderRequest.CreateOrderLine ln : req.getLines()) {
            if (ln == null)
                continue;
            Integer qty = (ln.getQuantity() == null || ln.getQuantity() <= 0) ? 1 : ln.getQuantity();
            MenuItemEntity menuItem = null;
            if (ln.getItemId() != null) {
                menuItem = menuItemRepository.findById(ln.getItemId()).orElse(null);
            }
            BigDecimal unitPrice = (ln.getPrice() != null)
                    ? ln.getPrice()
                    : (menuItem != null ? menuItem.getPrice() : BigDecimal.ZERO);
            String nameSnapshot = (ln.getName() != null && !ln.getName().isBlank())
                    ? ln.getName()
                    : (menuItem != null ? menuItem.getName() : null);
            OrderItemEntity li = OrderItemEntity.builder().order(input).menuItem(menuItem).quantity(qty)
                    .unitPrice(unitPrice).nameSnapshot(nameSnapshot)
                    .imageSnapshot(menuItem != null ? menuItem.getImage() : null)
                    .categorySnapshot(menuItem != null ? menuItem.getCategory() : null)
                    .subcategorySnapshot(menuItem != null ? menuItem.getSubcategory() : null).build();
            lines.add(li);
        }
        if (!lines.isEmpty()) {
            input.setLines(lines);
        }
    }

    // If all non-expired orders for the table are payment completed, mark table
    // AVAILABLE
    private void updateTableAvailabilityForTable(TableEntity table, String tenant) {
        if (table == null)
            return;
        String code = table.getCode();
        if (code == null || code.isBlank())
            return;
        List<OrderEntity> list;
        list = (tenant == null)
                ? orderRepository.findByTable_Code(code)
                : orderRepository.findByTable_CodeAndTenant_Code(code, tenant);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        boolean anyActive = false;
        for (OrderEntity e : list) {
            boolean expired = (e.getSessionExpiresAt() != null && e.getSessionExpiresAt().isBefore(now));
            if (!expired && e.getStatus() != OrderStatus.PAYMENT_COMPLETED) {
                anyActive = true;
                break;
            }
        }
        if (!anyActive && table.getStatus() != com.qrmatik.server.model.TableStatus.AVAILABLE) {
            table.setStatus(com.qrmatik.server.model.TableStatus.AVAILABLE);
            tableRepository.save(table);
        }
    }
}
