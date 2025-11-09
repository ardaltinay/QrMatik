package com.qrmatik.server.service;

import com.qrmatik.server.dto.TenantBootstrapUsersRequest;
import com.qrmatik.server.dto.TenantInsertRequest;
import com.qrmatik.server.model.PlanType;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.model.UserEntity;
import com.qrmatik.server.model.UserRole;
import com.qrmatik.server.repository.TenantRepository;
import com.qrmatik.server.exception.PlanLimitExceededException;
import com.qrmatik.server.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantAdminService {
    private static final Pattern CODE_RE = Pattern.compile("^(?=.{1,63}$)[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
    private static final Pattern DOMAIN_RE = Pattern
            .compile("^(?i:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)(?:\\.(?i:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?))+$");
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
    public Optional<TenantEntity> create(TenantInsertRequest req) {
        if (req.getCode() == null || req.getCode().isBlank())
            return Optional.empty();
        String code = req.getCode().trim();
        if (!CODE_RE.matcher(code).matches())
            return Optional.empty();
        if (tenantRepository.findByCode(code).isPresent())
            return Optional.empty();
        PlanType plan = PlanType.fromString(req.getPlan());
        TenantEntity e = TenantEntity.builder().code(code).name(req.getName()).ownerName(req.getOwnerName())
                .ownerEmail(req.getOwnerEmail()).logoUrl(req.getLogoUrl()).primaryColor(req.getPrimaryColor())
                .accentColor(req.getAccentColor()).configJson(req.getConfig()).plan(plan != null ? plan : PlanType.FREE)
                .build();
        // Custom domain only for PRO; validate and ensure uniqueness
        String cd = req.getCustomDomain();
        if (cd != null && !cd.isBlank() && (plan == PlanType.PRO)) {
            String dom = cd.trim().toLowerCase();
            if (DOMAIN_RE.matcher(dom).matches()) {
                if (tenantRepository.findByCustomDomain(dom).isEmpty()) {
                    e.setCustomDomain(dom);
                }
            }
        }
        return Optional.of(tenantRepository.save(e));
    }

    @Transactional
    public Optional<TenantEntity> update(String id, TenantInsertRequest req) {
        Optional<TenantEntity> found = getById(id);
        if (found.isEmpty())
            return Optional.empty();
        TenantEntity e = found.get();
        if (req.getName() != null)
            e.setName(req.getName());
        if (req.getOwnerName() != null)
            e.setOwnerName(req.getOwnerName());
        if (req.getOwnerEmail() != null)
            e.setOwnerEmail(req.getOwnerEmail());
        if (req.getLogoUrl() != null)
            e.setLogoUrl(req.getLogoUrl());
        if (req.getPrimaryColor() != null)
            e.setPrimaryColor(req.getPrimaryColor());
        if (req.getAccentColor() != null)
            e.setAccentColor(req.getAccentColor());
        if (req.getConfig() != null)
            e.setConfigJson(req.getConfig());
        if (req.getPlan() != null) {
            PlanType plan = PlanType.fromString(req.getPlan());
            if (plan != null)
                e.setPlan(plan);
            // If plan downgraded from PRO, clear customDomain
            if (plan != PlanType.PRO) {
                e.setCustomDomain(null);
            }
        }
        if (req.getCustomDomain() != null) {
            String cd = req.getCustomDomain();
            String dom = cd == null ? null : cd.trim().toLowerCase();
            if (dom == null || dom.isBlank()) {
                e.setCustomDomain(null);
            } else {
                // Only allow for PRO
                PlanType plan = e.getPlan() == null ? PlanType.FREE : e.getPlan();
                if (plan == PlanType.PRO && DOMAIN_RE.matcher(dom).matches()) {
                    var other = tenantRepository.findByCustomDomain(dom).orElse(null);
                    if (other == null || other.getId().equals(e.getId())) {
                        e.setCustomDomain(dom);
                    }
                }
            }
        }
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
            insertUser(tenant, req.getAdminUsername(), "ADMIN", req.getAdminPassword());
        }
        if (req.getKitchenUsername() != null && req.getKitchenPassword() != null) {
            insertUser(tenant, req.getKitchenUsername(), "KITCHEN", req.getKitchenPassword());
        }
        if (req.getBarUsername() != null && req.getBarPassword() != null) {
            insertUser(tenant, req.getBarUsername(), "BAR", req.getBarPassword());
        }
        return true;
    }

    private void insertUser(TenantEntity tenant, String username, String role, String rawPassword) {
        var existing = userRepository.findTopByUsernameAndTenant_CodeOrderByCreatedTimeDesc(username, tenant.getCode());
        UserEntity u = existing.orElseGet(() -> UserEntity.builder().username(username).tenant(tenant).build());
        // If creating a new user (not updating) enforce FREE tenant limit
        if (existing.isEmpty()) {
            PlanType plan = tenant.getPlan() == null ? PlanType.FREE : tenant.getPlan();
            if (plan == PlanType.FREE) {
                long cur = userRepository.countByTenant_Code(tenant.getCode());
                    if (cur >= 3L) {
                        throw new PlanLimitExceededException(
                                "Ücretsiz sürüm için maksimum 3 kullanıcı oluşturabilirsiniz.");
                }
            }
        }
        var er = UserRole.fromString(role);
        if (er == null)
            er = UserRole.STAFF;
        u.setRole(er);
        if (rawPassword != null && !rawPassword.isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(rawPassword));
        }
        userRepository.save(u);
    }
}
