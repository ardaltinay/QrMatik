package com.qrmatik.server.repository;

import com.qrmatik.server.model.TenantEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {
  Optional<TenantEntity> findByCode(String code);

  Optional<TenantEntity> findByCodeIgnoreCase(String code);

  Optional<TenantEntity> findByCustomDomain(String customDomain);

  List<TenantEntity> findByPendingEffectiveDateLessThanEqual(LocalDate date);
}
