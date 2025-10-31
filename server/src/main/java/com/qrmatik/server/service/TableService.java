package com.qrmatik.server.service;

import com.qrmatik.server.dto.TableUpsertRequest;
import com.qrmatik.server.model.TableEntity;
import com.qrmatik.server.model.TableStatus;
import com.qrmatik.server.model.TenantEntity;
import com.qrmatik.server.repository.TableRepository;
import com.qrmatik.server.repository.TenantRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TableService {

    private final TableRepository tableRepository;
    private final TenantRepository tenantRepository;

    public TableService(TableRepository tableRepository, TenantRepository tenantRepository) {
        this.tableRepository = tableRepository;
        this.tenantRepository = tenantRepository;
    }

    public List<TableEntity> listForCurrentTenant() {
        String tcode = TenantContext.getTenant();
        if (tcode == null || tcode.isBlank()) return List.of();
        return tableRepository.findByTenant_Code(tcode);
    }

    @Transactional
    public Optional<TableEntity> createForCurrentTenant(TableUpsertRequest req) {
        String tcode = TenantContext.getTenant();
        if (tcode == null || tcode.isBlank()) return Optional.empty();
        if (req == null || req.getCode() == null || req.getCode().isBlank()) return Optional.empty();
        String code = req.getCode().trim();
        // enforce per-tenant uniqueness via findByCodeAndTenant_Code
        if (tableRepository.findByCodeAndTenant_Code(code, tcode).isPresent()) {
            return Optional.empty();
        }
        TenantEntity tenant = tenantRepository.findByCode(tcode).orElse(null);
        if (tenant == null) return Optional.empty();
        TableEntity e = TableEntity.builder()
                .code(code)
                .description(req.getDescription())
                .status(req.getStatus() == null ? TableStatus.AVAILABLE : req.getStatus())
                .tenant(tenant)
                .build();
        return Optional.of(tableRepository.save(e));
    }

    @Transactional
    public Optional<TableEntity> updateForCurrentTenant(UUID id, TableUpsertRequest req) {
        String tcode = TenantContext.getTenant();
        if (tcode == null || tcode.isBlank()) return Optional.empty();
        if (id == null) return Optional.empty();
        TableEntity e = tableRepository.findById(id).orElse(null);
        if (e == null || e.getTenant() == null || e.getTenant().getCode() == null
                || !tcode.equals(e.getTenant().getCode())) {
            return Optional.empty();
        }
        if (req.getCode() != null && !req.getCode().isBlank()) {
            String newCode = req.getCode().trim();
            if (!newCode.equals(e.getCode())) {
                // check uniqueness within tenant
                if (tableRepository.findByCodeAndTenant_Code(newCode, tcode).isPresent()) {
                    return Optional.empty();
                }
                e.setCode(newCode);
            }
        }
        if (req.getDescription() != null) e.setDescription(req.getDescription());
        if (req.getStatus() != null) e.setStatus(req.getStatus());
        return Optional.of(tableRepository.save(e));
    }

    @Transactional
    public boolean deleteForCurrentTenant(UUID id) {
        String tcode = TenantContext.getTenant();
        if (tcode == null || tcode.isBlank()) return false;
        if (id == null) return false;
        TableEntity e = tableRepository.findById(id).orElse(null);
        if (e == null || e.getTenant() == null || e.getTenant().getCode() == null
                || !tcode.equals(e.getTenant().getCode())) return false;
        try {
            tableRepository.delete(e);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
