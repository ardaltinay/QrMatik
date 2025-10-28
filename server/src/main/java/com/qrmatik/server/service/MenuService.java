package com.qrmatik.server.service;

import com.qrmatik.server.model.MenuItemEntity;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.repository.TenantRepository;
import com.qrmatik.server.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MenuService {
    private final MenuItemRepository repository;
    private final ImageService imageService;
    private final TenantRepository tenantRepository;

    public MenuService(MenuItemRepository repository, ImageService imageService, TenantRepository tenantRepository) {
        this.repository = repository;
        this.imageService = imageService;
        this.tenantRepository = tenantRepository;
    }

    public List<MenuItemEntity> listForTenant(String tenant) {
        if (tenant == null) return repository.findAll();
        try { return repository.findByTenant_Code(tenant); }
        catch (NoSuchMethodError | Exception e) { return repository.findAll(); }
    }

    public MenuItemEntity create(MenuItemEntity m, String tenant) {
        if (tenant != null && (m.getTenant() == null || m.getTenant().getCode() == null)) {
            try {
                Optional<TenantEntity> t = tenantRepository.findByCode(tenant);
                t.ifPresent(m::setTenant);
            } catch (Exception ignored) {}
        }
        return repository.save(m);
    }

    public Optional<MenuItemEntity> update(UUID id, MenuItemEntity patch, String tenant) {
        Optional<MenuItemEntity> opt = repository.findById(id);
        if (opt.isEmpty()) return Optional.empty();
        MenuItemEntity e = opt.get();
        // tenant guard
        if (tenant != null) {
            String code = (e.getTenant() != null ? e.getTenant().getCode() : null);
            if (code != null && !tenant.equals(code)) return Optional.empty();
        }
        if (patch.getName() != null) e.setName(patch.getName());
        if (patch.getPrice() != null) e.setPrice(patch.getPrice());
        if (patch.getCategory() != null) e.setCategory(patch.getCategory());
        if (patch.getSubcategory() != null) e.setSubcategory(patch.getSubcategory());
        if (patch.getImage() != null) e.setImage(patch.getImage());
        return Optional.of(repository.save(e));
    }

    public Map<String, Object> uploadImage(UUID id, MultipartFile file, String tenant) throws IOException {
        Optional<MenuItemEntity> opt = repository.findById(id);
        if (opt.isEmpty()) return null;
        MenuItemEntity item = opt.get();
        if (tenant == null) {
            tenant = (item.getTenant() != null ? item.getTenant().getCode() : null);
        }
        ImageService.SavedImages saved = imageService.saveMenuItemImage(tenant, id, file);
        item.setImage(saved.mediumUrl);
        repository.save(item);
        return Map.of(
                "original", saved.originalUrl,
                "medium", saved.mediumUrl,
                "thumb", saved.thumbUrl,
                "image", item.getImage()
        );
    }

    public boolean delete(UUID id, String tenant) {
        Optional<MenuItemEntity> opt = repository.findById(id);
        if (opt.isEmpty()) return false;
        MenuItemEntity item = opt.get();
        if (tenant != null) {
            String code = (item.getTenant() != null ? item.getTenant().getCode() : null);
            if (code != null && !tenant.equals(code)) return false;
        }
        repository.deleteById(id);
        return true;
    }
}
