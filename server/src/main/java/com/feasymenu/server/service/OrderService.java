package com.feasymenu.server.service;

import com.feasymenu.server.dto.CreateOrderRequest;
import com.feasymenu.server.exception.BadRequestException;
import com.feasymenu.server.exception.TableUnavailableException;
import com.feasymenu.server.model.MenuItemEntity;
import com.feasymenu.server.model.OrderEntity;
import com.feasymenu.server.model.OrderItemEntity;
import com.feasymenu.server.model.OrderStatus;
import com.feasymenu.server.model.PlanType;
import com.feasymenu.server.model.TableEntity;
import com.feasymenu.server.model.TableStatus;
import com.feasymenu.server.model.TenantEntity;
import com.feasymenu.server.repository.MenuItemRepository;
import com.feasymenu.server.repository.OrderRepository;
import com.feasymenu.server.repository.TableRepository;
import com.feasymenu.server.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;
    private final TenantRepository tenantRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${app.table.linger-minutes:30}")
    private int tableLingerMinutes;

    public OrderService(OrderRepository orderRepository, TableRepository tableRepository,
            MenuItemRepository menuItemRepository, TenantRepository tenantRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.menuItemRepository = menuItemRepository;
        this.tenantRepository = tenantRepository;
        this.messagingTemplate = messagingTemplate;
    }

    private void notifyUpdate(OrderEntity order) {
        if (order != null && order.getTenant() != null) {
            String tenantCode = order.getTenant().getCode();
            // Notify tenant-specific topic (e.g., for kitchen/admin dashboard)
            messagingTemplate.convertAndSend("/topic/orders/" + tenantCode, order);
            // Notify session-specific topic (e.g., for customer mobile app)
            messagingTemplate.convertAndSend("/topic/session/" + order.getSessionId(), order);
        }
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

    public Optional<OrderEntity> getIfViewable(String id, String sid, boolean authenticated) {
        Optional<OrderEntity> o = getById(id);
        if (o.isEmpty())
            return Optional.empty();
        OrderEntity e = o.get();

        // Auto-expire check
        if (e.getStatus() != OrderStatus.CANCELED && e.getStatus() != OrderStatus.PAYMENT_COMPLETED
                && e.getStatus() != OrderStatus.EXPIRED) {
            if (e.getSessionExpiresAt() != null && e.getSessionExpiresAt().isBefore(Instant.now())) {
                e.setStatus(OrderStatus.EXPIRED);
                orderRepository.save(e);
                notifyUpdate(e);
            }
        }

        if (!authenticated) {
            if (sid == null || sid.isBlank()) {
                return Optional.empty();
            }
            String realSid = e.getSessionId();
            if (realSid == null || !realSid.equals(sid)) {
                return Optional.empty();
            }
        }
        return Optional.of(e);
    }

    public static record SessionViewResult(List<OrderEntity> orders, boolean anyNonExpired) {
    }

    public SessionViewResult bySessionForView(String sessionId, String tenant, ZoneId zoneId) {
        List<OrderEntity> entities = bySessionForTenant(sessionId, tenant);
        Instant now = Instant.now();
        boolean anyNonExpired = false;
        for (OrderEntity e : entities) {
            boolean isExpired = (e.getSessionExpiresAt() != null && e.getSessionExpiresAt().isBefore(now));
            if (!isExpired) {
                anyNonExpired = true;
                break;
            }
        }
        return new SessionViewResult(entities, anyNonExpired);
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

        // Personel (ADMIN/KITCHEN/BAR) iptali: sadece NEW, PREPARING, READY
        // durumlarında izin ver
        if (parsed == OrderStatus.CANCELED) {
            OrderStatus cur = order.getStatus();
            if (cur != OrderStatus.NEW && cur != OrderStatus.PREPARING && cur != OrderStatus.READY) {
                return Optional.empty();
            }
        }

        order.setStatus(parsed);
        // adjust session expiry based on status
        if (parsed == OrderStatus.SERVED) {
            order.setSessionExpiresAt(Instant.now().plus(3, ChronoUnit.HOURS));
        } else if (parsed == OrderStatus.PAYMENT_COMPLETED) {
            // Ödeme tamamlandıktan sonra müşterinin masada kalması için bekleme süresi
            order.setSessionExpiresAt(Instant.now().plus(Math.max(0, tableLingerMinutes), ChronoUnit.MINUTES));
        } else if (parsed == OrderStatus.CANCELED) {
            // İptalde de oturum hemen kapansın
            order.setSessionExpiresAt(Instant.now());
        } else if (parsed == OrderStatus.EXPIRED) {
            // Süresi dolmuş sipariş: oturumu kapat
            order.setSessionExpiresAt(Instant.now());
        } else {
            // sliding window: ensure at least 24h from now
            Instant min = Instant.now().plus(24, ChronoUnit.HOURS);
            if (order.getSessionExpiresAt() == null || order.getSessionExpiresAt().isBefore(min)) {
                order.setSessionExpiresAt(min);
            }
        }
        // Stok iadesi: ilk kez CANCELED'a geçiliyorsa ve inventoryApplied true ise geri
        // al
        if (parsed == OrderStatus.CANCELED && (order.getInventoryApplied() != null && order.getInventoryApplied())) {
            try {
                restoreInventory(order);
                order.setInventoryApplied(false); // iade yapıldı
            } catch (Exception ex) {
                log.warn("Inventory restore failed for order {}: {}", order.getId(), ex.getMessage());
            }
        }
        OrderEntity saved = orderRepository.save(order);
        notifyUpdate(saved);
        // Sipariş durumuna göre masanın uygunluğunu güncelle
        if (parsed == OrderStatus.PAYMENT_COMPLETED || parsed == OrderStatus.CANCELED
                || parsed == OrderStatus.EXPIRED) {
            try {
                updateTableAvailabilityForTable(saved.getTable(), tenant);
            } catch (Exception ignore) {
            }
        }
        return Optional.of(saved);
    }

    /**
     * Mark orders older than 1 day as EXPIRED if they are not already in a terminal
     * state. Returns the number of orders updated.
     */
    public int markExpiredOrders() {
        Instant cutoff = Instant.now().minus(1, ChronoUnit.DAYS);
        List<OrderEntity> all = orderRepository.findAll();
        int count = 0;
        for (OrderEntity e : all) {
            try {
                if (e == null)
                    continue;
                Instant created = e.getCreatedTime();
                if (created == null || !created.isBefore(cutoff))
                    continue;
                OrderStatus st = e.getStatus();
                if (st == OrderStatus.SERVED || st == OrderStatus.PAYMENT_COMPLETED || st == OrderStatus.CANCELED
                        || st == OrderStatus.EXPIRED) {
                    continue;
                }
                e.setStatus(OrderStatus.EXPIRED);
                e.setSessionExpiresAt(Instant.now());
                orderRepository.save(e);
                // Update table availability similar to canceled/completed
                try {
                    updateTableAvailabilityForTable(e.getTable(),
                            e.getTenant() != null ? e.getTenant().getCode() : null);
                } catch (Exception ignore) {
                }
                count++;
            } catch (Exception ignore) {
                // ignore individual failures to prevent breaking the sweep
            }
        }
        return count;
    }

    public Optional<OrderEntity> create(CreateOrderRequest req, String tenant) {
        OrderEntity input = new OrderEntity();
        input.setCreatedTime(Instant.now());
        input.setStatus(req.getStatus() != null ? OrderStatus.fromString(req.getStatus()) : OrderStatus.NEW);
        input.setSessionExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        String incomingSession = req.getSessionId();
        input.setSessionId((incomingSession == null || incomingSession.isBlank())
                ? UUID.randomUUID().toString()
                : incomingSession);

        String tcode = (tenant != null && !tenant.isBlank()) ? tenant.trim() : null;

        // Enforce cross-tenant session rule: existing session must belong to the same
        // tenant
        if (incomingSession != null && !incomingSession.isBlank()) {
            try {
                List<OrderEntity> existing = orderRepository.findBySessionId(incomingSession);
                String existingTenant = null;
                if (existing != null) {
                    for (OrderEntity e : existing) {
                        if (e != null && e.getTenant() != null && e.getTenant().getCode() != null
                                && !e.getTenant().getCode().isBlank()) {
                            existingTenant = e.getTenant().getCode();
                            break; // take the first non-null tenant
                        }
                    }
                }
                if (existingTenant != null) {
                    // If request tenant (from context) is present and different, reject
                    if (tcode != null && !tcode.equals(existingTenant)) {
                        throw new BadRequestException("error.order.sessionMismatch");
                    }
                    // Otherwise, align order tenant to the existing session's tenant
                    try {
                        tenantRepository.findByCode(existingTenant).ifPresent(input::setTenant);
                    } catch (Exception ignore) {
                    }
                    // Also make sure we use the canonical session id (in case of formatting
                    // differences)
                    input.setSessionId(incomingSession);
                }
            } catch (Exception ignore) {
                // do not fail hard on lookup issues; proceed with normal flow
            }
        }

        // validate tableCode if provided and set relation
        if (req.getTableCode() != null && !req.getTableCode().isBlank()) {
            var tableOpt = (tcode != null)
                    ? tableRepository.findByCodeAndTenant_Code(req.getTableCode(), tcode)
                    : tableRepository.findByCode(req.getTableCode());
            if (tableOpt.isEmpty()) {
                throw new BadRequestException("error.order.tableNotFound");
            }
            var table = tableOpt.get();
            // tenant tutarlılığı: hem paramda tenant hem tablonun tenantı varsa ve
            // farklıysa reddet
            String tableTenant = (table.getTenant() != null ? table.getTenant().getCode() : null);
            if (tcode != null && tableTenant != null && !tcode.equals(tableTenant)) {
                throw new BadRequestException("error.order.tenantMismatch");
            }
            // order.tenant öncelik: param -> tablonun tenantı
            if (tcode != null) {
                tenantRepository.findByCode(tcode).ifPresent(t -> {
                    if (!t.isActive()) throw new BadRequestException("error.tenant.suspended");
                    input.setTenant(t);
                });
            } else if (tableTenant != null) {
                tenantRepository.findByCode(tableTenant).ifPresent(t -> {
                    if (!t.isActive()) throw new BadRequestException("error.tenant.suspended");
                    input.setTenant(t);
                });
            }

            input.setTable(table);
            // UNAVAILABLE masaya sipariş kabul etmeyelim
            if (table.getStatus() == TableStatus.UNAVAILABLE) {
                throw new TableUnavailableException("error.order.tableUnavailable");
            }
            // Sipariş oluşturulunca masa BUSY olsun
            if (table.getStatus() != TableStatus.BUSY) {
                table.setStatus(TableStatus.BUSY);
                tableRepository.save(table);
            }
        } else {
            // Masa belirtilmemişse, sadece tenant paramıyla ilerleyelim
            if (tcode != null) {
                tenantRepository.findByCode(tcode).ifPresent(input::setTenant);
            }
        }

        // En az 1 satır zorunlu
        if (req.getLines() == null || req.getLines().isEmpty()) {
            throw new BadRequestException("error.order.atLeastOneLineRequired");
        }

        buildOrderLinesFromRequest(input, (input.getTenant() != null ? input.getTenant().getCode() : tcode), req);

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

        // Menü kalemleri tenant tutarlılığı kontrolü
        String orderTenant = (input.getTenant() != null ? input.getTenant().getCode() : null);
        if (orderTenant != null) {
            for (OrderItemEntity li : input.getLines()) {
                if (li.getMenuItem() != null && li.getMenuItem().getTenant() != null) {
                    String miTenant = li.getMenuItem().getTenant().getCode();
                    if (miTenant != null && !orderTenant.equals(miTenant)) {
                        throw new BadRequestException("error.order.tenantMismatch");
                    }
                }
            }
        }

        // Envanter düşümü (yalnız PRO plan & stok aktif kalemler)
        applyInventoryIfEligible(input);
        OrderEntity saved = orderRepository.save(input);
        notifyUpdate(saved);
        return Optional.of(saved);
    }

    public Optional<OrderEntity> cancelBySession(String id, String tenant, String sessionId) {
        if (sessionId == null || sessionId.isBlank())
            return Optional.empty();
        Optional<OrderEntity> o = getById(id);
        if (o.isEmpty())
            return Optional.empty();
        OrderEntity order = o.get();
        // tenant guard
        if (tenant != null) {
            TenantEntity ordTenant = order.getTenant();
            String oc = (ordTenant != null ? ordTenant.getCode() : null);
            if (oc != null && !tenant.equals(oc))
                return Optional.empty();
        }
        // session guard
        if (!sessionId.equals(order.getSessionId()))
            return Optional.empty();
        // only NEW within 2 minutes
        Instant now = Instant.now();
        Instant created = order.getCreatedTime();
        boolean within2min = created != null && !created.isBefore(now.minus(2, ChronoUnit.MINUTES));
        if (order.getStatus() != OrderStatus.NEW || !within2min)
            return Optional.empty();

        order.setStatus(OrderStatus.CANCELED);
        order.setSessionExpiresAt(now);
        OrderEntity saved = orderRepository.save(order);
        try {
            updateTableAvailabilityForTable(saved.getTable(), tenant);
        } catch (Exception ignore) {
        }
        return Optional.of(saved);
    }

    public Optional<OrderEntity> requestBillBySession(String id, String tenant, String sessionId) {
        if (sessionId == null || sessionId.isBlank())
            return Optional.empty();
        Optional<OrderEntity> o = getById(id);
        if (o.isEmpty())
            return Optional.empty();
        OrderEntity order = o.get();
        // tenant guard
        if (tenant != null) {
            TenantEntity ordTenant = order.getTenant();
            String oc = (ordTenant != null ? ordTenant.getCode() : null);
            if (oc != null && !tenant.equals(oc))
                return Optional.empty();
        }
        // session guard
        if (!sessionId.equals(order.getSessionId()))
            return Optional.empty();
        // allow only after SERVED, and ignore if already terminal
        OrderStatus cur = order.getStatus();
        if (cur == null || cur == OrderStatus.CANCELED || cur == OrderStatus.PAYMENT_COMPLETED
                || cur == OrderStatus.EXPIRED)
            return Optional.empty();
        if (cur != OrderStatus.SERVED && cur != OrderStatus.BILL_REQUESTED)
            return Optional.empty();
        order.setStatus(OrderStatus.BILL_REQUESTED);
        // keep current session expiry; do not shorten here
        OrderEntity saved = orderRepository.save(order);
        return Optional.of(saved);
    }

    public int closeSessionsForTable(String tableCode, String tenant) {
        String tcode = (tenant != null ? tenant.trim() : null);
        if (log.isDebugEnabled()) {
            log.debug("closeSessionsForTable run tableCode={}, tenant={}", tableCode, tcode);
        }
        // Masayı bul (önce tenant'a göre, yoksa legacy fallback: sadece code)
        TableEntity tableRef = null;
        try {
            Optional<TableEntity> tOpt;
            if (tcode == null || tcode.isBlank()) {
                tOpt = tableRepository.findByCode(tableCode);
            } else {
                tOpt = tableRepository.findByCodeAndTenant_Code(tableCode, tcode);
            }
            if (tOpt.isPresent()) {
                var t = tOpt.get();
                tableRef = t;
                // Eğer tablo farklı bir tenant'a aitse, id ile sipariş arama yoluna gitmeyelim
                if (t.getTenant() != null && tcode != null && !tcode.equals(t.getTenant().getCode())) {
                    tableRef = null; // id tabanlı arama yerine aşağıda code+tenant ile devam edilecek
                }
            }
        } catch (Exception ignored) {
        }

        List<OrderEntity> list;
        if (tableRef != null && tableRef.getId() != null) {
            // Tablo bulundu, tenant varsa tenant'a göre, yoksa sadece tableId ile güvenli
            // daraltma
            if (tcode != null && !tcode.isBlank()) {
                list = orderRepository.findByTable_IdAndTenant_Code(tableRef.getId(), tcode);
            } else {
                list = orderRepository.findByTable_Id(tableRef.getId());
            }
        } else {
            // Tablo bulunamadıysa ve tenant yoksa güvenli davran: işlem yapma
            if (tcode == null || tcode.isBlank()) {
                if (log.isWarnEnabled()) {
                    log.warn("closeSessionsForTable aborted: table not found for code={}, tenant is null/blank",
                            tableCode);
                }
                return 0;
            }
            // Tenant biliniyorsa code+tenant ile daralt
            list = orderRepository.findByTable_CodeAndTenant_Code(tableCode, tcode);
        }

        Instant now = Instant.now();
        int count = 0;
        for (OrderEntity e : list) {
            boolean changed = false;
            if (e.getSessionExpiresAt() == null || e.getSessionExpiresAt().isAfter(now)) {
                e.setSessionExpiresAt(now);
                changed = true;
            }
            // Admin masa kapatma: ödeme tamamlanmadıysa CANCELED olarak işaretle (null
            // dahil)
            if (e.getStatus() == null || e.getStatus() != OrderStatus.PAYMENT_COMPLETED) {
                e.setStatus(OrderStatus.CANCELED);
                changed = true;
            }
            if (changed) {
                orderRepository.save(e);
                count++;
            }
        }
        // Siparişler iptal edildikten sonra masanın uygunluk durumunu güncelle
        try {
            if (tableRef != null) {
                updateTableAvailabilityForTable(tableRef, tcode);
            } else {
                // tablo yeniden bulunup uygunluk güncellenebilir
                if (tcode != null && !tcode.isBlank()) {
                    tableRepository.findByCodeAndTenant_Code(tableCode, tcode)
                            .ifPresent(t -> updateTableAvailabilityForTable(t, tcode));
                } else {
                    tableRepository.findByCode(tableCode).ifPresent(t -> updateTableAvailabilityForTable(t, tcode));
                }
            }
        } catch (Exception ignore) {
        }
        if (log.isDebugEnabled()) {
            log.debug("closeSessionsForTable updated orders count={}", count);
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
            // Fiyat otoritesi sunucuda: menü kalemi varsa onun fiyatını kullan; yoksa
            // istekten geleni düş
            BigDecimal unitPrice = (menuItem != null && menuItem.getPrice() != null)
                    ? menuItem.getPrice()
                    : (ln.getPrice() != null ? ln.getPrice() : BigDecimal.ZERO);
            String nameSnapshot = (ln.getName() != null && !ln.getName().isBlank())
                    ? ln.getName()
                    : (menuItem != null ? menuItem.getName() : null);
            OrderItemEntity li = OrderItemEntity.builder().order(input).menuItem(menuItem).quantity(qty)
                    .unitPrice(unitPrice).nameSnapshot(nameSnapshot)
                    .imageSnapshot(menuItem != null ? menuItem.getImage() : null)
                    .categorySnapshot(menuItem != null ? menuItem.getCategory() : null)
                    .subcategorySnapshot(menuItem != null ? menuItem.getSubcategory() : null).note(ln.getNote())
                    .build();
            lines.add(li);
        }
        if (!lines.isEmpty()) {
            input.setLines(lines);
        }
    }

    private void applyInventoryIfEligible(OrderEntity order) {
        try {
            String tenantCode = order.getTenant() != null ? order.getTenant().getCode() : null;
            if (tenantCode == null)
                return;
            TenantEntity t = tenantRepository.findByCode(tenantCode).orElse(null);
            if (t == null || t.getPlan() == null || t.getPlan() != PlanType.PRO)
                return; // yalnız PRO
            if (order.getLines() == null || order.getLines().isEmpty())
                return;
            // Önce yeterli stok var mı kontrol et
            for (OrderItemEntity li : order.getLines()) {
                MenuItemEntity mi = li.getMenuItem();
                if (mi == null)
                    continue;
                Boolean enabled = mi.getStockEnabled();
                Integer qty = mi.getStockQuantity();
                if (enabled != null && enabled && qty != null) {
                    int need = (li.getQuantity() != null ? li.getQuantity() : 1);
                    if (qty < need) {
                        throw new BadRequestException("error.order.insufficientStock");
                    }
                }
            }
            // Yeterli ise düş
            for (OrderItemEntity li : order.getLines()) {
                MenuItemEntity mi = li.getMenuItem();
                if (mi == null)
                    continue;
                Boolean enabled = mi.getStockEnabled();
                Integer qty = mi.getStockQuantity();
                if (enabled != null && enabled && qty != null) {
                    int need = (li.getQuantity() != null ? li.getQuantity() : 1);
                    mi.setStockQuantity(Math.max(0, qty - need));
                    menuItemRepository.save(mi);
                }
            }
            order.setInventoryApplied(true);
        } catch (BadRequestException ex) {
            throw ex; // ileriye fırlat
        } catch (Exception ex) {
            log.warn("Inventory apply failed for order {}: {}", order.getId(), ex.getMessage());
        }
    }

    private void restoreInventory(OrderEntity order) {
        if (order.getLines() == null)
            return;
        String tenantCode = order.getTenant() != null ? order.getTenant().getCode() : null;
        if (tenantCode == null)
            return;
        TenantEntity t = tenantRepository.findByCode(tenantCode).orElse(null);
        if (t == null || t.getPlan() == null || t.getPlan() != PlanType.PRO)
            return; // yalnız PRO
        for (OrderItemEntity li : order.getLines()) {
            MenuItemEntity mi = li.getMenuItem();
            if (mi == null)
                continue;
            Boolean enabled = mi.getStockEnabled();
            Integer qty = mi.getStockQuantity();
            if (enabled != null && enabled) {
                int add = (li.getQuantity() != null ? li.getQuantity() : 1);
                if (qty == null) {
                    // Sınırsız stok modunda iade anlamsız, skip
                    continue;
                }
                mi.setStockQuantity(qty + add);
                menuItemRepository.save(mi);
            }
        }
    }

    // Masa uygunluğunu güncelle: aktif (süresi dolmamış) sipariş varsa BUSY, yoksa
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
        Instant now = Instant.now();
        boolean anyActive = false;
        for (OrderEntity e : list) {
            boolean expired = (e.getSessionExpiresAt() != null && e.getSessionExpiresAt().isBefore(now));
            // PAYMENT_COMPLETED da session bitene kadar masayı meşgul saysın
            if (!expired && e.getStatus() != OrderStatus.CANCELED) {
                anyActive = true;
                break;
            }
        }
        if (anyActive) {
            if (table.getStatus() != TableStatus.BUSY) {
                table.setStatus(TableStatus.BUSY);
                tableRepository.save(table);
            }
        } else {
            if (table.getStatus() != TableStatus.AVAILABLE) {
                table.setStatus(TableStatus.AVAILABLE);
                tableRepository.save(table);
            }
        }
    }
}
