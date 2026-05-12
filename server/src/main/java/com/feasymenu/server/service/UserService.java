package com.feasymenu.server.service;

import com.feasymenu.server.dto.UserInsertRequest;
import com.feasymenu.server.exception.PlanLimitExceededException;
import com.feasymenu.server.model.PlanType;
import com.feasymenu.server.model.TenantEntity;
import com.feasymenu.server.model.UserEntity;
import com.feasymenu.server.model.UserRole;
import com.feasymenu.server.repository.TenantRepository;
import com.feasymenu.server.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    private final UserRepository repository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.super.username:super}")
    private String superUsername;

    @Value("${app.init.super.password:super123}")
    private String superPassword;

    public UserService(UserRepository repository, TenantRepository tenantRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initSuperUser() {
        Optional<UserEntity> existing = repository.findByUsername(superUsername);
        if (existing.isEmpty()) {
            UserEntity u = new UserEntity();
            u.setUsername(superUsername);
            u.setPasswordHash(passwordEncoder.encode(superPassword));
            u.setRole(UserRole.SUPERADMIN);
            repository.save(u);
            System.out.println("[Init] Super user created: " + superUsername);
        } else {
            // Parolayı her zaman .env dosyasındaki ile senkronize et (güncelleme durumları
            // için)
            UserEntity u = existing.get();
            u.setPasswordHash(passwordEncoder.encode(superPassword));
            u.setRole(UserRole.SUPERADMIN); // Role'ün SUPERADMIN olduğundan emin ol
            repository.save(u);
            System.out.println("[Init] Super user password synchronized from environment.");
        }
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
        // Check if username already exists in this tenant
        if (tenant != null) {
            if (repository.findByUsernameAndTenant_Code(req.getUsername(), tenant).isPresent()) {
                throw new com.feasymenu.server.exception.BadRequestException("error.user.usernameExists");
            }
        } else {
            // For superadmin/global users
            if (repository.findByUsername(req.getUsername()).isPresent()) {
                throw new com.feasymenu.server.exception.BadRequestException("error.user.usernameExists");
            }
        }

        // Enforce plan user limits in create method
        // Enforce FREE plan user limit: max 3 users
        if (tenant != null) {
            TenantEntity t = tenantRepository.findByCode(tenant).orElse(null);
            if (t != null) {
                PlanType plan = t.getPlan() == null ? PlanType.FREE : t.getPlan();
                if (plan == PlanType.FREE) {
                    long currentCount = repository.countByTenant_Code(tenant);
                    if (currentCount >= 3) {
                        throw new PlanLimitExceededException("error.plan.staffLimitFree");
                    }
                }
            }
        }

        UserEntity u = new UserEntity();
        u.setUsername(req.getUsername());
        {
            var er = UserRole.fromString(req.getRole());
            if (er == null) {
                // fallback: treat unknown as STAFF
                er = UserRole.SALOON;
            }
            u.setRole(er);
        }
        if (tenant != null) {
            Optional<TenantEntity> t = tenantRepository.findByCode(tenant);
            t.ifPresent(u::setTenant);
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            validatePassword(req.getPassword());
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
        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            // If username is changing, check for uniqueness
            if (!u.getUsername().equals(req.getUsername())) {
                if (tenant != null) {
                    if (repository.findByUsernameAndTenant_Code(req.getUsername(), tenant).isPresent()) {
                        throw new com.feasymenu.server.exception.BadRequestException("error.user.usernameExists");
                    }
                } else {
                    if (repository.findByUsername(req.getUsername()).isPresent()) {
                        throw new com.feasymenu.server.exception.BadRequestException("error.user.usernameExists");
                    }
                }
            }
            u.setUsername(req.getUsername());
        }
        if (req.getRole() != null && !req.getRole().isBlank()) {
            // Update role logic
            var er = UserRole.fromString(req.getRole());
            if (er == null)
                er = UserRole.SALOON;
            u.setRole(er);
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            validatePassword(req.getPassword());
            u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        return Optional.of(repository.save(u));
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("error.auth.passwordTooShort");
        }
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
