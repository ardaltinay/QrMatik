package com.qrmatik.server.repository;

import com.qrmatik.server.model.MenuItemEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, UUID> {
    java.util.List<com.qrmatik.server.model.MenuItemEntity> findByTenant_Code(String tenantCode);
}
