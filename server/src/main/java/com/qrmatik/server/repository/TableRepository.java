package com.qrmatik.server.repository;

import com.qrmatik.server.model.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, UUID> {
    Optional<TableEntity> findByCode(String code);
    Optional<TableEntity> findByCodeAndTenant_Code(String code, String tenantCode);
}
