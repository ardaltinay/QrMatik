package com.qrmatik.server.service;

import com.qrmatik.server.model.MenuItemEntity;
import com.qrmatik.server.model.OrderStatus;
import com.qrmatik.server.model.PlanType;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.repository.MenuItemRepository;
import com.qrmatik.server.repository.OrderItemRepository;
import com.qrmatik.server.repository.TenantRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MenuService {
    private final MenuItemRepository repository;
    private final ImageService imageService;
    private final TenantRepository tenantRepository;
    private final OrderItemRepository orderItemRepository;
    private final PlanGuard planGuard;

    public MenuService(MenuItemRepository repository, ImageService imageService, TenantRepository tenantRepository,
            OrderItemRepository orderItemRepository, PlanGuard planGuard) {
        this.repository = repository;
        this.imageService = imageService;
        this.tenantRepository = tenantRepository;
        this.orderItemRepository = orderItemRepository;
        this.planGuard = planGuard;
    }

    public List<MenuItemEntity> listForTenant(String tenant) {
        if (tenant == null) {
            // No tenant resolved: do not leak global menu, return empty list
            return Collections.emptyList();
        }
        return repository.findByTenant_Code(tenant);
    }

    public MenuItemEntity create(MenuItemEntity m, String tenant) {
        if (tenant != null && (m.getTenant() == null || m.getTenant().getCode() == null)) {
            try {
                Optional<TenantEntity> t = tenantRepository.findByCode(tenant);
                t.ifPresent(m::setTenant);
            } catch (Exception ignored) {
            }
        }
        // plan enforcement
        if (tenant != null) {
            planGuard.assertCanCreateMenuItem(tenant);
        } else if (m.getTenant() != null && m.getTenant().getCode() != null) {
            planGuard.assertCanCreateMenuItem(m.getTenant().getCode());
        }
        return repository.save(m);
    }

    public Optional<MenuItemEntity> update(UUID id, MenuItemEntity patch, String tenant) {
        Optional<MenuItemEntity> opt = repository.findById(id);
        if (opt.isEmpty())
            return Optional.empty();
        MenuItemEntity e = opt.get();
        // tenant guard
        if (tenant != null) {
            String code = (e.getTenant() != null ? e.getTenant().getCode() : null);
            if (code != null && !tenant.equals(code))
                return Optional.empty();
        }
        if (patch.getName() != null)
            e.setName(patch.getName());
        if (patch.getPrice() != null)
            e.setPrice(patch.getPrice());
        if (patch.getCategory() != null)
            e.setCategory(patch.getCategory());
        if (patch.getSubcategory() != null)
            e.setSubcategory(patch.getSubcategory());
        if (patch.getImage() != null)
            e.setImage(patch.getImage());
        // Stock-related fields require plan check
        if (patch.getStockEnabled() != null || patch.getStockQuantity() != null) {
            // will throw PlanFeatureUnavailableException when not allowed
            if (tenant != null) {
                planGuard.assertStockFeature(tenant);
            } else if (e.getTenant() != null && e.getTenant().getCode() != null) {
                planGuard.assertStockFeature(e.getTenant().getCode());
            }
            if (patch.getStockEnabled() != null)
                e.setStockEnabled(patch.getStockEnabled());
            if (patch.getStockQuantity() != null)
                e.setStockQuantity(patch.getStockQuantity());
        }
        return Optional.of(repository.save(e));
    }

    public Map<String, Object> uploadImage(UUID id, MultipartFile file, String tenant) throws IOException {
        // Basic validation moved to service layer for reusability
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Boş dosya");
        }
        final long MAX_BYTES = 2 * 1024 * 1024;
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Dosya çok büyük (max 2MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null
                || (!contentType.equalsIgnoreCase("image/jpeg") && !contentType.equalsIgnoreCase("image/png")
                        && !contentType.equalsIgnoreCase("image/webp") && !contentType.equalsIgnoreCase("image/gif"))) {
            throw new IllegalArgumentException("Desteklenmeyen içerik türü");
        }

        Optional<MenuItemEntity> opt = repository.findById(id);
        if (opt.isEmpty())
            return null;
        MenuItemEntity item = opt.get();
        if (tenant == null) {
            tenant = (item.getTenant() != null ? item.getTenant().getCode() : null);
        }
        ImageService.SavedImages saved = imageService.saveMenuItemImage(tenant, id, file);
        item.setImage(saved.mediumUrl());
        repository.save(item);
        return Map.of("medium", saved.mediumUrl(), "thumb", saved.thumbUrl(), "image", item.getImage());
    }

    public boolean delete(UUID id, String tenant) {
        Optional<MenuItemEntity> opt = repository.findById(id);
        if (opt.isEmpty())
            return false;
        MenuItemEntity item = opt.get();
        if (tenant != null) {
            String code = (item.getTenant() != null ? item.getTenant().getCode() : null);
            if (code != null && !tenant.equals(code))
                return false;
        }
        repository.deleteById(id);
        return true;
    }

    public List<MenuItemEntity> popular(String tenant, int limit) {
        // FREE planda popüler ürünler özelliği yok
        try {
            if (tenant != null) {
                var t = tenantRepository.findByCode(tenant).orElse(null);
                if (t != null && (t.getPlan() == null || t.getPlan() == PlanType.FREE)) {
                    return Collections.emptyList();
                }
            }
        } catch (Exception ignored) {
        }
        int lim = (limit <= 0 ? 4 : Math.min(limit, 50));
        List<Object[]> rows = orderItemRepository.topMenuItemCounts(tenant, OrderStatus.CANCELED,
                PageRequest.of(0, lim));
        if (rows == null || rows.isEmpty())
            return Collections.emptyList();
        List<UUID> ids = new ArrayList<>();
        for (Object[] r : rows) {
            if (r != null && r.length >= 1 && r[0] != null) {
                try {
                    ids.add((UUID) r[0]);
                } catch (ClassCastException cce) {
                    if (r[0] instanceof String s) {
                        try {
                            ids.add(UUID.fromString(s));
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
        }
        List<MenuItemEntity> list = repository.findAllById(ids);
        Map<UUID, MenuItemEntity> map = new HashMap<>();
        for (MenuItemEntity m : list) {
            if (m != null && m.getId() != null)
                map.put(m.getId(), m);
        }
        List<MenuItemEntity> out = new ArrayList<>();
        for (UUID id : ids) {
            MenuItemEntity x = map.get(id);
            if (x != null)
                out.add(x);
        }
        return out;
    }

    public List<MenuItemEntity> listForStock(String tenant) {
        if (tenant != null) {
            planGuard.assertStockFeature(tenant);
        }
        return listForTenant(tenant);
    }
}
