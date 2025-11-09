package com.qrmatik.server.repository;

import com.qrmatik.server.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByTable_Code(String tableCode);

    List<OrderEntity> findBySessionId(String sessionId);

    List<OrderEntity> findByTenant_Code(String tenantCode);

    List<OrderEntity> findByTable_CodeAndTenant_Code(String tableCode, String tenantCode);

    List<OrderEntity> findBySessionIdAndTenant_Code(String sessionId, String tenantCode);

    List<OrderEntity> findByTable_IdAndTenant_Code(UUID tableId, String tenantCode);

    List<OrderEntity> findByTable_Id(UUID tableId);
}
