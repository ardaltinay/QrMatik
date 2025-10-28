package com.qrmatik.server.service;

import com.qrmatik.server.dto.UserUpsertRequest;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.model.UserEntity;
import com.qrmatik.server.repository.TenantRepository;
import com.qrmatik.server.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public UserEntity create(UserUpsertRequest req, String tenant) {
        UserEntity u = new UserEntity();
        u.setUsername(req.getUsername());
        u.setRole(req.getRole());
        if (tenant != null) {
            Optional<TenantEntity> t = tenantRepository.findByCode(tenant);
            t.ifPresent(u::setTenant);
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        return repository.save(u);
    }

    public Optional<UserEntity> update(String id, UserUpsertRequest req, String tenant) {
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
        if (req.getRole() != null && !req.getRole().isBlank())
            u.setRole(req.getRole());
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
