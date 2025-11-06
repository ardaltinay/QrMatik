package com.qrmatik.server.repository;

import com.qrmatik.server.model.TableEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, UUID> {
  Optional<TableEntity> findByCode(String code);

  Optional<TableEntity> findByCodeAndTenant_Code(String code, String tenantCode);

  List<TableEntity> findByTenant_Code(String tenantCode);

  long countByTenant_Code(String tenantCode);
}
