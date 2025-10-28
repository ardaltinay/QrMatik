package com.qrmatik.server.repository;

import com.qrmatik.server.model.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, UUID> {
	java.util.List<com.qrmatik.server.model.MenuItemEntity> findByTenant_Code(String tenantCode);
}
