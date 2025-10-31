package com.qrmatik.server.service;

import com.qrmatik.server.dto.TenantBootstrapUsersRequest;
import com.qrmatik.server.dto.TenantUpsertRequest;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.model.UserEntity;
import com.qrmatik.server.repository.TenantRepository;
import com.qrmatik.server.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantAdminService {
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TenantAdminService(TenantRepository tenantRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<TenantEntity> list() {
        return tenantRepository.findAll();
    }

    public Optional<TenantEntity> getById(String id) {
        try {
            return tenantRepository.findById(UUID.fromString(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<TenantEntity> create(TenantUpsertRequest req) {
        if (req.getCode() == null || req.getCode().isBlank())
            return Optional.empty();
        if (tenantRepository.findByCode(req.getCode()).isPresent())
            return Optional.empty();
        TenantEntity e = TenantEntity.builder()
                .code(req.getCode().trim())
                .name(req.getName())
                .logoUrl(req.getLogoUrl())
                .primaryColor(req.getPrimaryColor())
                .accentColor(req.getAccentColor())
                .configJson(req.getConfig())
                .build();
        return Optional.of(tenantRepository.save(e));
    }

    @Transactional
    public Optional<TenantEntity> update(String id, TenantUpsertRequest req) {
        Optional<TenantEntity> found = getById(id);
        if (found.isEmpty())
            return Optional.empty();
        TenantEntity e = found.get();
        if (req.getName() != null)
            e.setName(req.getName());
        if (req.getLogoUrl() != null)
            e.setLogoUrl(req.getLogoUrl());
        if (req.getPrimaryColor() != null)
            e.setPrimaryColor(req.getPrimaryColor());
        if (req.getAccentColor() != null)
            e.setAccentColor(req.getAccentColor());
        if (req.getConfig() != null)
            e.setConfigJson(req.getConfig());
        return Optional.of(tenantRepository.save(e));
    }

    @Transactional
    public boolean delete(String id) {
        Optional<TenantEntity> found = getById(id);
        if (found.isEmpty())
            return false;
        tenantRepository.delete(found.get());
        return true;
    }

    @Transactional
    public boolean bootstrapUsers(String tenantCode, TenantBootstrapUsersRequest req) {
        Optional<TenantEntity> tOpt = tenantRepository.findByCode(tenantCode);
        if (tOpt.isEmpty())
            return false;
        TenantEntity tenant = tOpt.get();

        // Create or update users if usernames provided
        if (req.getAdminUsername() != null && req.getAdminPassword() != null) {
            upsertUser(tenant, req.getAdminUsername(), "ADMIN", req.getAdminPassword());
        }
        if (req.getKitchenUsername() != null && req.getKitchenPassword() != null) {
            upsertUser(tenant, req.getKitchenUsername(), "KITCHEN", req.getKitchenPassword());
        }
        if (req.getBarUsername() != null && req.getBarPassword() != null) {
            upsertUser(tenant, req.getBarUsername(), "BAR", req.getBarPassword());
        }
        return true;
    }

    private void upsertUser(TenantEntity tenant, String username, String role, String rawPassword) {
        var existing = userRepository.findByUsernameAndTenant_Code(username, tenant.getCode());
        UserEntity u = existing.orElseGet(() -> UserEntity.builder().username(username).tenant(tenant).build());
        u.setRole(role);
        if (rawPassword != null && !rawPassword.isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(rawPassword));
        }
        userRepository.save(u);
    }
}
