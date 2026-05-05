package com.feasymenu.server.service;

import com.feasymenu.server.dto.TableInsertRequest;
import com.feasymenu.server.model.TableEntity;
import com.feasymenu.server.model.TableStatus;
import com.feasymenu.server.model.TenantEntity;
import com.feasymenu.server.repository.TableRepository;
import com.feasymenu.server.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TableService {

    private final TableRepository tableRepository;
    private final TenantRepository tenantRepository;
    private final PlanGuard planGuard;

    public TableService(TableRepository tableRepository, TenantRepository tenantRepository, PlanGuard planGuard) {
        this.tableRepository = tableRepository;
        this.tenantRepository = tenantRepository;
        this.planGuard = planGuard;
    }

    public List<TableEntity> listForCurrentTenant() {
        String tcode = TenantContext.getTenant();
        if (tcode == null || tcode.isBlank())
            return List.of();
        return tableRepository.findByTenant_Code(tcode);
    }

    public Optional<TableEntity> createForCurrentTenant(TableInsertRequest req) {
        String tcode = TenantContext.getTenant();
        if (tcode == null || tcode.isBlank())
            return Optional.empty();
        if (req == null || req.getCode() == null || req.getCode().isBlank())
            return Optional.empty();
        String code = req.getCode().trim();
        // enforce per-tenant uniqueness via findByCodeAndTenant_Code
        if (tableRepository.findByCodeAndTenant_Code(code, tcode).isPresent()) {
            return Optional.empty();
        }
        TenantEntity tenant = tenantRepository.findByCode(tcode).orElse(null);
        if (tenant == null)
            return Optional.empty();
        // plan enforcement
        planGuard.assertCanCreateTable(tcode);
        TableEntity e = TableEntity.builder().code(code).description(req.getDescription()).capacity(req.getCapacity())
                .status(req.getStatus() == null ? TableStatus.AVAILABLE : req.getStatus()).tenant(tenant).build();
        return Optional.of(tableRepository.save(e));
    }

    public Optional<TableEntity> updateForCurrentTenant(UUID id, TableInsertRequest req) {
        String tcode = TenantContext.getTenant();
        if (tcode == null || tcode.isBlank())
            return Optional.empty();
        if (id == null)
            return Optional.empty();
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
        if (req.getDescription() != null)
            e.setDescription(req.getDescription());
        if (req.getCapacity() != null)
            e.setCapacity(req.getCapacity());
        if (req.getStatus() != null)
            e.setStatus(req.getStatus());
        return Optional.of(tableRepository.save(e));
    }

    public boolean deleteForCurrentTenant(UUID id) {
        String tcode = TenantContext.getTenant();
        if (tcode == null || tcode.isBlank())
            return false;
        if (id == null)
            return false;
        TableEntity e = tableRepository.findById(id).orElse(null);
        if (e == null || e.getTenant() == null || e.getTenant().getCode() == null
                || !tcode.equals(e.getTenant().getCode()))
            return false;
        try {
            tableRepository.delete(e);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
