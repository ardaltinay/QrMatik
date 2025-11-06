package com.qrmatik.server.service;

import com.qrmatik.server.dto.UserInsertRequest;
import com.qrmatik.server.model.PlanType;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.model.UserEntity;
import com.qrmatik.server.model.UserRole;
import com.qrmatik.server.exception.PlanFeatureUnavailableException;
import com.qrmatik.server.repository.TenantRepository;
import com.qrmatik.server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, TenantRepository tenantRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserEntity> listForTenant(String tenant) {
        if (tenant == null)
            return repository.findAll();
        try {
            return repository.findByTenant_Code(tenant);
        } catch (Exception e) {
            return repository.findAll();
        }
    }

    public Optional<UserEntity> getById(String id) {
        try {
            return repository.findById(UUID.fromString(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public UserEntity create(UserInsertRequest req, String tenant) {
        // FREE planda kitchen/bar kullanıcıları oluşturulamaz
        if (tenant != null && req.getRole() != null) {
            String r = req.getRole().trim().toLowerCase();
            if ("kitchen".equals(r) || "bar".equals(r)) {
                TenantEntity t = tenantRepository.findByCode(tenant).orElse(null);
                if (t != null && (t.getPlan() == null || t.getPlan() == PlanType.FREE)) {
                    throw new PlanFeatureUnavailableException(
                            "Mutfak ve Bar desteği Standart veya Pro planlarda mevcuttur.");
                }
            }
        }
        UserEntity u = new UserEntity();
        u.setUsername(req.getUsername());
        {
            var er = UserRole.fromString(req.getRole());
            if (er == null) {
                // fallback: treat unknown as STAFF
                er = UserRole.STAFF;
            }
            u.setRole(er);
        }
        if (tenant != null) {
            Optional<TenantEntity> t = tenantRepository.findByCode(tenant);
            t.ifPresent(u::setTenant);
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        return repository.save(u);
    }

    public Optional<UserEntity> update(String id, UserInsertRequest req, String tenant) {
        Optional<UserEntity> found = getById(id);
        if (found.isEmpty())
            return Optional.empty();
        UserEntity u = found.get();
        if (tenant != null) {
            String code = (u.getTenant() != null ? u.getTenant().getCode() : null);
            if (code != null && !tenant.equals(code))
                return Optional.empty();
        }
        if (req.getUsername() != null && !req.getUsername().isBlank())
            u.setUsername(req.getUsername());
        if (req.getRole() != null && !req.getRole().isBlank()) {
            // FREE planda kitchen/bar rolüne güncellenemez
            if (tenant != null) {
                String r = req.getRole().trim().toLowerCase();
                if ("kitchen".equals(r) || "bar".equals(r)) {
                    TenantEntity t = tenantRepository.findByCode(tenant).orElse(null);
                    if (t != null && (t.getPlan() == null || t.getPlan() == PlanType.FREE)) {
                        throw new PlanFeatureUnavailableException(
                                "Mutfak ve Bar desteği Standart veya Pro planlarda mevcuttur.");
                    }
                }
            }
            var er = UserRole.fromString(req.getRole());
            if (er == null)
                er = UserRole.STAFF;
            u.setRole(er);
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        return Optional.of(repository.save(u));
    }

    public boolean delete(String id, String tenant) {
        Optional<UserEntity> found = getById(id);
        if (found.isEmpty())
            return false;
        UserEntity u = found.get();
        if (tenant != null) {
            String code = (u.getTenant() != null ? u.getTenant().getCode() : null);
            if (code != null && !tenant.equals(code))
                return false;
        }
        repository.delete(u);
        return true;
    }
}
